(ns app.main.redux
  (:require [app.redux.verticals :as verts]))

(def this-ns (keyword (namespace ::r)))

(def on-address-change (verts/create-action ::on-address-change))

(defn address-selector [state]
  (get-in state [this-ns :address]))

(def default-state
  {:address nil
   :address? false})

(def reducer-slice
  {(keyword (namespace ::r))
   (verts/handle-actions
     {::on-address-change (fn [state {val :payload}] (assoc state :address val))}
     default-state)})
