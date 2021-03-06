(ns app.main.redux
  (:require [web3-cljs.core :as web3]
            [redux.verticals :as verts]))

(def on-address-change (verts/create-action ::on-address-change))

(defn address-selector [state]
  (get-in state [::state :address]))

(defn valid?-selector [state]
  (and
    (get-in state [::state :dirty?])
    (get-in state [::state :valid?])))

(def default-state
  {:address ""
   :valid? false
   :dirty? false})

(def reducer-slice
  {::state
   (verts/handle-actions
     {::on-address-change
      (fn [state {address :payload}]
        (-> state
          (assoc :address address)
          (assoc :dirty? true)
          (assoc :valid? (web3/address? address))))}

     default-state)})
