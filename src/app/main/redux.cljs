(ns app.main.redux
  (:require [app.redux.verticals :as verts]))

(def on-address-change (verts/create-action ::on-change))

(defn address-selector [state]
  (::address state))

(def default-state
  {::address nil
   ::address? false})

(def reducer-slice
  {(keyword (namespace ::r))
   (verts/handle-actions
     {::on-change (fn [state {val :payload}] (assoc state :address val))}
     default-state)})
