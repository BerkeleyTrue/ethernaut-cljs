(ns app.redux.core
  (:require [helix.core :refer [create-context]]
            [helix.hooks :refer [use-context]]))


(def react-redux-context (create-context))

(set! (.-displayName react-redux-context) "react-redux-context")

(defn use-redux-context []
  (use-context react-redux-context))

(defn use-store []
  (:store (use-redux-context)))

(defn use-dispatch []
  (:dispatch (use-store)))

(defn create-store [reducer preloaded-state]
  (let [state (atom preloaded-state)
        dispatch (fn [action]
                   (swap! state (reducer @state action)))

        get-state (fn [] @state)]

    {:dispatch dispatch
     :get-state get-state}))
