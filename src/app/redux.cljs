(ns app.redux
  (:require [redux.verticals :as verts]))

(def types
  (verts/create-types
    ::on-mount))

(def on-mount (verts/create-action ::on-mount))
