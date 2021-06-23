(ns app.provider
  (:require [clojure.set :refer [map-invert]]
            [redux.verticals :as verts :refer [create-action]]
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

(def chain-to-id-map
  (->>
    chains
    (map-invert)
    (seq)
    (map (fn [[k v]] [(keyword k) v]))
    (into (sorted-map))))

; actions
(def provider-detected (verts/create-action ::detected))
(def chain-changed (verts/create-action ::chain-changed #(if (string? %) (js/parseInt % 16) %)))
(def chain-changed-error (verts/create-action ::chain-changed-error))

(def connect-wallet (verts/create-action ::connect-wallet))
(def connect-wallet-complete (verts/create-action ::connect-wallet-complete))
(def connect-wallet-error (verts/create-action ::connect-wallet-error))

(def blocker-header-emitted (create-action ::blocker-header-emitted))

(def default-state
  {:detected? false
   :run? false
   :chain-id nil
   :rinkeby? false
   :address ""
   :block-num 0})


; selectors
(def detected?-selector #(get-in % [::state :detected?]))
(def run?-selector #(get-in % [::state :run?]))
(def rinkeby?-selector #(get-in % [::state :rinkeby?]))
(def chain-id-selector #(get-in % [::state :chain-id]))
(def chain-name-selector (comp #(get chains % "NA") chain-id-selector))
(def address-selector #(get-in % [::state :address]))
(def block-num-selector #(get-in % [::state :block-num]))


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
          (assoc :chain-id chainId)))

      ::connect-wallet-complete
      (fn [state {address :payload}]
        (->
          state
          (assoc :address address)))
      ::blocker-header-emitted
      (fn [state {{number :number} :payload}]
        (-> state
          (assoc :block-num number)))}

     default-state)})

(defonce sub (atom nil))

(defn ^:dev/before-load unsub-on-reload []
  (when @sub
    (.unsubscribe @sub #(print "unsubcribed"))
    (reset! sub nil)))

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

            (when detected?
              (let [wb (web3. provider)
                    block-header-sub (.subscribe (.-eth wb) "newBlockHeaders")]

                (reset! sub block-header-sub)
                (.on block-header-sub "data" (comp dispatch blocker-header-emitted #(js->clj % :keywordize-keys true)))
                (.on block-header-sub "error" (comp dispatch blocker-header-emitted))
                ; get chain id if it wasn't found on the provider
                (when (nil? chain-id)
                  (->
                    (.getChainId (.-eth wb))
                    (.then chain-changed)
                    (.catch chain-changed-error)
                    (.then dispatch)))))))

        (when (= action-type ::connect-wallet)
          (let [detected? (detected?-selector (get-state))
                provider @provider-ref]
            (when (and detected? provider)
              (->
                provider
                (.request #js {:method "eth_requestAccounts"})
                (.then first)
                (.then connect-wallet-complete)
                (.catch connect-wallet-error)
                (.then dispatch)))))

        result))))
