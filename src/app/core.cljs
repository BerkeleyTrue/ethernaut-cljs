(ns app.core
  (:require
    [helix.core :refer [$]]
    ["react-dom" :refer [render]]
    [app.layout :as layout]))

(defn ^:dev/after-load create-app []
  (render ($ layout/App) (js/document.getElementById "app")))

(defn ^:export main [] (create-app))
