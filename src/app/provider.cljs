(ns app.provider
  (:require [redux.verticals :as verts]
            ["@metamask/detect-provider" :as detect-provider]
            ["web3" :as web3]
            [app.redux :as app]))


(defonce ^:private provider-ref (atom nil))

(def ^:private chains
  {1 "mainnent"
   3 "ropsten"
   4 "rinkeby"
   5 "goerli"
   42 "kovan"})

; actions
(def provider-detected (verts/create-action ::detected))
(def connect-wallet (verts/create-action ::connect))
(def chain-changed (verts/create-action ::chain-changed #(if (string? %) (js/parseInt % 16) %)))
(def chain-changed-error (verts/create-action ::chain-changed-error))

(def default-state
  {:detected? false
   :run? false
   :chain-id nil
   :rinkeby? false})

; selectors
(def detected?-selector #(get-in % [::state :detected?]))
(def run?-selector #(get-in % [::state :run?]))
(def rinkeby?-selector #(get-in % [::state :rinkeby?]))
(def chain-id-selector #(get-in % [::state :chain-id]))
(def chain-name-selector (comp #(get chains % "NA") chain-id-selector))

(def reducer-slicer
  {::state
   (verts/handle-actions
     {::detected
      (fn [state {{:keys [detected? chainId]} :payload}]
        (->
          state
          (assoc :detected? detected?)
          (assoc :run? true)
          (assoc :chain-id chainId)))

      ::chain-changed
      (fn [state {chainId :payload}]
        (->
          state
          (assoc :chain-id chainId)))}

     default-state)})

(defn provider-middleware
  [{:keys [dispatch get-state]}]
  (fn [next]
    (fn [action]

      (let [result (next action)
            action-type (:type action)]

        (when (= action-type (get app/types :on-mount))
          (->
            (detect-provider)
            (.then
              (fn [^js/object provider]
               (when provider
                 (.on provider "chainChanged" (comp dispatch chain-changed))
                 (reset! provider-ref provider)
                 (dispatch (provider-detected {:detected? true})))))

            (.catch (fn [err] (print err)))))

        (when (= action-type ::detected)
          (let [chain-id (chain-id-selector (get-state))
                detected? (detected?-selector (get-state))
                provider @provider-ref]

            (when (and detected? (nil? chain-id))
              (let [wb (web3. provider)]
                (->
                  (.getChainId (.-eth wb))
                  (.then chain-changed)
                  (.catch chain-changed-error)
                  (.then dispatch))))))

        result))))
