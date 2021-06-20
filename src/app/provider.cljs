(ns app.provider
  (:require [redux.verticals :as verts]
            ["@metamask/detect-provider" :as detect-provider]
            ["web3" :as web3]
            [app.redux :as app]))


(defonce ^:private web3-ref (atom nil))

(def ^:private chains
  {1 "mainnent"
   3 "ropsten"
   4 "rinkeby"
   5 "goerli"
   42 "kovan"})

(defn- get-chain-name [chain-id] (get chains chain-id "NA"))

; actions
(def provider-detected (verts/create-action ::detected))
(def connect-wallet (verts/create-action ::connect))
(def chain-changed (verts/create-action ::chain-changed))

(def default-state
  {:detected? false
   :run? false
   :chainId nil
   :rinkeby? false})

(def detected?-selector #(get-in % [::state :detected?]))
(def run?-selector #(get-in % [::state :run?]))
(def rinkeby?-selector #(get-in % [::state :rinkeby?]))

(def reducer-slicer
  {::state
   (verts/handle-actions
     {::detected
      (fn [state {{:keys [detected? chainId]} :payload}]
        (->
          state
          (assoc :detected? detected?)
          (assoc :run? true)
          (assoc :chainId chainId)))

      ::chain-changed
      (fn [state {chainId :payload}]
        (->
          state
          (assoc :chainId chainId)))}

     default-state)})

(defn provider-middleware
  [{:keys [dispatch]}]
  (fn [next]
    (fn [action]

      (let [result (next action)]
        (when (= (:type action) (get app/types :on-mount))
          (->
            (detect-provider)
            (.then
              (fn [^js/object provider]
               (when provider
                 (.on provider "chainChanged" (comp dispatch chain-changed))
                 (dispatch (provider-detected {:detected? true})))))
                 ; (when (not (.-chainId provider))))))

            (.catch (fn [err] (print err)))))

        result))))
