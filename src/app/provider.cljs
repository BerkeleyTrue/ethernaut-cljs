(ns app.provider
  (:require [redux.verticals :as verts]
            ["@metamask/detect-provider" :as detect-provider]
            [app.redux :as app]))


(defonce ^:private provider (atom nil))

; (defn connect []
;   (if (= @provider nil)))

(def ^:private chains
  {1 "mainnent"
   3 "ropsten"
   4 "rinkeby"
   5 "goerli"
   42 "kovan"})

(defn- get-chain [provider]
  ())


(defn- get-chain-name [chain-id] (get chains chain-id "NA"))

; actions
(def provider-detected (verts/create-action ::detected))
(def connect-wallet (verts/create-action ::connect))

(def default-state
  {:detected? false
   :run? false
   :rinkeby? false})

(def detected?-selector #(get-in % [::state :detected?]))
(def run?-selector #(get-in % [::state :run?]))
(def rinkeby?-selector #(get-in % [::state :rinkeby?]))

(def reducer-slicer
  {::state
   (verts/handle-actions
     {::detected
      (fn [state {detected? :payload}]
        (->
          state
          (assoc :detected? detected?)
          (assoc :run? true)))}
     default-state)})

(defn provider-middleware
  [{:keys [dispatch]}]
  (fn [next]
    (fn [action]

      (let [result (next action)]
        (when (= (:type action) (get app/types :on-mount))
          (->
            (detect-provider)
            (.then #(do
                     (reset! provider %)
                     (dispatch (provider-detected (boolean %)))))

            (.catch (fn [err] (print err)))))

        result))))
