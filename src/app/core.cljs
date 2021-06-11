(ns app.core
  (:require
    [helix.core :refer [$]]
    ["react-dom" :refer [render]]
    [app.layout :as layout]))


(defn ^:export main []
  (render ($ layout/main) (js/document.getElementById "app")))
