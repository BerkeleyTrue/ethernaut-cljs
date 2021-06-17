(ns app.core
  (:require
    [helix.core :refer [$]]
    [react-dom :refer [render]]
    [app.redux.core :refer [react-redux-context create-store]]
    [app.redux.verticals :as verts]
    [app.redux.dev-tool-ext :refer [dev-tools-enhancer]]
    [app.layout :as layout]
    [app.main.redux :as main-redux]))

(defonce get-state (atom (constantly nil)))

(defn ^:dev/after-load create-app
  ([] (create-app nil))
  ([default-state]
   (let [default-state (or (@get-state) default-state)
         store (create-store
                 (verts/combine-reducers main-redux/reducer-slice)
                 default-state
                 (dev-tools-enhancer))]

     (reset! get-state (:get-state store))
     (render ($ (.-Provider react-redux-context)
                {:value store
                 ;; key to force re-renders on hot reload
                 :key (rand)}

                ($ layout/App))
             (js/document.getElementById "app")))))

(defn ^:export main [] (create-app))
