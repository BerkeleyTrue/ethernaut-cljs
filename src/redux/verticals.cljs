(ns redux.verticals
  (:require ["invariant" :as invariant]
            [clojure.string :as str]))

(def action-delimiter "|")

(defn- wrap-payload-creator [payload-creator]
  (fn
    [head & args]
    (if
      (instance? js/Error head) head
      (apply payload-creator (into [head] args)))))

(defn create-types [& types]
  (->>
    types
    (map #(vector (keyword (name %)) %))
    (into (sorted-map))))

(comment
  (= (create-types ::foo ::bar) {:foo ::foo :bar ::bar}))

(defn create-action
  ([type] (create-action type identity nil))
  ([type payload-creator] (create-action type payload-creator nil))
  ([type payload-creator meta-creator]
   (invariant
     (fn? payload-creator)
     (str
       "Expected payload-creator for "
       (str type)
       " to be a function but got: "
       payload-creator))

   (let [meta? (fn? meta-creator)
         final-payload-creator (if
                                 (= identity payload-creator) identity
                                 (wrap-payload-creator payload-creator))]

    (fn [& args]
      (let [action {:type type}
            payload (apply final-payload-creator args)]
        (cond-> action
          (not (nil? payload)) (assoc :payload payload)
          meta? (assoc :meta (apply meta-creator args))))))))

(defn- add-meta-event [action event]
  (assoc-in action [:meta :event] event))

(defn compose-actions
  "
  Compose multiple actions in an event->command[] pattern
  A single event can dispatch commands which will trigger
  side effects in other fractals of an app
  "
  [event & commands]
  (fn composed-action [& args]
    (let [event-action (apply event args)
          event-type (:type event-action)]
      (into
        [event-action]
        (map
          (fn [f]
            (apply
              (comp #(add-meta-event % event-type) f)
              args))
          commands)))))

(defn reduce-reducers [default-state & reducers]
  (invariant
    (not (nil? default-state))
    (str "reduce-reducers expects an initial state but got " default-state))
  (invariant
    (seq reducers)
    (str "reducer-reducers expects at least one reducer but found " reducers))

  (run!
    #(invariant (fn? %) (str "reduce-reducers expects all reducers to be of type function but found " %))
    reducers)

  (fn [state action]
    (reduce
      (fn [new-state reducer]
        (reducer new-state action))
      state
      reducers)))

(defn handle-action
  ([type default-state] (handle-action type identity default-state))

  ([type reducer default-state]
   (let [types (str/split type action-delimiter)
         presenting-types (str/join types ", ")]
     (invariant (or (fn? reducer) (map? reducer))
                (str "Expected reducer for " presenting-types " to be a function or object with next and throw reducers"))
     (invariant
       (map? default-state)
       (str "default-state for reducer handling " presenting-types " should be defined"))

     (fn [state action]
       (let [state (or state default-state)
             ; convert type to string to match types array above
             type (str (:type action))]
         (if
           (or
             (not type)
             (not (some #(= type %) types))) state
           (reducer state action)))))))

(defn handle-actions [handler-map default-state]
  (invariant (map? handler-map) "Expected handler-map to be a plain map")
  (->>
    handler-map
    (mapv
      (fn [[type reducer]]
        (handle-action type reducer default-state)))
    (into [default-state])
    (apply reduce-reducers)))

(defn combine-reducers [reducer-map]
  (run!
    (fn [[key reducer]]
      (invariant
        (fn? reducer)
        (str "combine-reducer expected a function for key " key " but found " reducer)))
    reducer-map)

  (fn [state action]
    (reduce
      (fn [next-state [key reducer]]

        (let [prev-state (get state key)
              next-state-for-key (reducer prev-state action)]

          (invariant
            (not (undefined? next-state-for-key))
            (str
              "slice-reducer when called with (" (or (:type action) "_UNKNOWN_")  ") produced for key " key
              "a nil state."))

          (assoc next-state key next-state-for-key)))

      state
      reducer-map)))
