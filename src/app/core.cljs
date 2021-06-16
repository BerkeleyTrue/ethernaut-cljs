(ns app.core
  (:require
    [helix.core :refer [$]]
    [react-dom :refer [render]]
    [app.redux.core :refer [react-redux-context create-store]]
    [app.layout :as layout]
    [app.main.redux :as main-redux]))

(defonce default-state (merge {} main-redux/default-state))

(defn ^:dev/after-load create-app [default-state]
  (let [store (create-store (fn [] {}) default-state)]

    (render ($ (.-Provider react-redux-context)
               {:value store}
               ($ layout/App))
            (js/document.getElementById "app"))))

(defn ^:export main [] (create-app default-state))
