(ns app.web3
  (:require ["web3-utils" :as utils]))

(defn address? [address]
  (js->clj (.isAddress utils address)))
