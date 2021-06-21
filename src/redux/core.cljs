(ns redux.core
  (:require [helix.core :refer [create-context]]
            [helix.hooks :as h]
            ["invariant" :as invariant]))

(def react-redux-context (create-context))

(set! (.-displayName react-redux-context) "react-redux-context")

(defn use-redux-context []
  (h/use-context react-redux-context))

(defn use-store []
  (use-redux-context))

(defn use-selector [selector]
  (invariant (fn? selector)
             (str "use-selector expects a function but found " selector))
  (h/use-debug-value selector)

  (let [[_ force-rerender] (h/use-reducer inc 0)
        store (use-store)

        state-ref (h/use-ref nil)
        selector-ref (h/use-ref nil)
        selected-state-ref (h/use-ref nil)

        subscriber (fn []
                     (let [state ((:get-state store))
                           selected-state (@selector-ref state)]

                       (when (not (= selected-state @selected-state-ref))
                         (reset! state-ref state)
                         (reset! selected-state-ref selected-state)
                         (force-rerender))))

        new-state ((:get-state store))
        new-selected-state (selector new-state)

        selected-state (if
                         (and
                           ;; if global state or selector fn have changed
                           (or (not (= selector @selector-ref))
                               (not (= new-state @state-ref)))
                           ;; and
                           (or
                             ;; prev is nil (initial run)
                             (not @selected-state-ref)
                             ;; or selected state has changed
                             (not (= @selected-state-ref new-selected-state))))
                         ;; update local selected-state
                         new-selected-state
                         ;; else use prev selected state
                         @selected-state-ref)]

    (h/use-layout-effect
      :once
      (reset! selector-ref selector)
      (reset! state-ref new-state)
      (reset! selected-state-ref selected-state))

    ;; updates selector state on store updates
    ;; triggers comp update using force-rerender
    (h/use-layout-effect
      [store]
      ((:subscribe store) subscriber))

    selected-state))

(defn use-dispatch []
  (:dispatch (use-store)))

(defn use-action [action-creator]
  (invariant
    (fn? action-creator)
    (str "use-action expected a function but received " action-creator))
  (let [dispatch (use-dispatch)]
    (comp dispatch action-creator)))

(defn create-store
  ([reducer preloaded-state]
   (let [state (atom preloaded-state)
         get-state (fn [] @state)

         subscriptions (atom #{})
         subscribe (fn [subscriber]
                     (invariant
                       (fn? subscriber)
                       (str "subscribe expects subscriber to be a function but found " (or subscriber "nil")))
                     (swap! subscriptions conj subscriber)
                     (fn [] (swap! subscriptions disj subscriber)))

         dispatch (fn [action]
                    (invariant
                      (and (map? action) (:type action))
                      (str "dispatch expects all actions to be a map with a type set but found " (or action "nil")))

                    (swap! state #(reducer % action))
                    (run! #(%) @subscriptions))]

     ;; dispatch INIT so that each reducer populates it's own initial state
     (dispatch {:type ::INIT})

     {:dispatch dispatch
      :get-state get-state
      :subscribe subscribe}))


  ([reducer preloaded-state enhancer]
   (invariant
     (fn? enhancer)
     (str "create-store expected enhancer to be a function but found " enhancer))

   ((enhancer create-store) reducer preloaded-state)))

(defn apply-middlewares [& middlewares]
  (fn am-enhancer
    [create-store]
    (fn am-create-store
      [reducer preloaded-state]
      (let [store (create-store reducer preloaded-state)
            get-state (:get-state store)
            store-dispatch (:dispatch store)
            dispatch-ref (atom (fn [] (throw (js/Error "Do not dispatch during middleware creation"))))
            dispatch (fn dispatch [action] (@dispatch-ref action))

            api {:get-state get-state
                 :dispatch dispatch}

            chain (->>
                    middlewares
                    (map #(% api))
                    (apply comp))

            chain-dispatch (chain store-dispatch)]

        (reset! dispatch-ref chain-dispatch)
        (assoc store :dispatch chain-dispatch)))))
