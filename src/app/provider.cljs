(ns app.provider
  (:require [redux.verticals :as verts]
            ["@metamask/detect-provider" :as detect-provider]))

(defonce ^:private provider (atom nil))

; (defn connect []
;   (if (= @provider nil)))

(def ^:private chains
  {1 "mainnent"
   3 "ropsten"
   4 "rinkeby"
   5 "goerli"
   42 "kovan"})

; (def types
;   (verts/create-types ::init))
(defn- get-chain [provider]
  ())


(defn- get-chain-name [chain-id] (get chains chain-id "NA"))

; actions
(def init-provider (verts/create-action ::init))
(def provider-detected (verts/create-action ::detected))
(def connect-wallet (verts/create-action ::connect))

(def default-state
  {:detected? false
   :rinkeby? false})

(def reducer-slicer
  {::state
   {::detected #(assoc % :detected? true)}})

(defn provider-middleware
  [{:keys [dispatch get-state]}]
  (fn [next]
    (fn [action]

      (let [result (next action)]
        (when (= (:type action) ::init)
          (->
            (detect-provider)
            (.then (fn [_provider]
                     (reset! provider _provider)
                     ()))

            (.catch (fn [err] (print err)))))

        result))))
