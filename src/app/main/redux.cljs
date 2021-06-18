(ns app.main.redux
  (:require [app.redux.verticals :as verts]))

(def on-address-change (verts/create-action ::on-address-change))

(defn address-selector [state]
  (get-in state [::state :address]))

(def default-state
  {:address ""
   :address? false})

(def reducer-slice
  {::state
   (verts/handle-actions
     {::on-address-change (fn [state {val :payload}] (assoc state :address val))}
     default-state)})
