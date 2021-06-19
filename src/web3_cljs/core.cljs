(ns web3-cljs.core
  (:require ["web3-utils" :as utils]))

(defn address? [address]
  (js->clj (.isAddress utils address)))
