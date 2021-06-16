(ns app.redux.core
  (:require [helix.core :refer [create-context]]
            [helix.hooks :refer [use-context use-debug-value]]
            ["invariant" :as invariant]))

(def react-redux-context (create-context))

(set! (.-displayName react-redux-context) "react-redux-context")

(defn use-redux-context []
  (use-context react-redux-context))

(defn use-store []
  (use-redux-context))

(defn use-selector [selector]
  (invariant (fn? selector)
             (str "use-selector expects a function but found " selector))
  (use-debug-value selector)
  (let [store (use-store)]
    (selector ((:get-state store)))))

(defn use-dispatch []
  (:dispatch (use-store)))

(defn use-action [action-creator]
  (invariant
    (fn? action-creator)
    (str "use-action expected a function but received " action-creator))
  (let [dispatch (use-dispatch)]
    (comp dispatch action-creator)))

(defn create-store [reducer preloaded-state]
  (let [state (atom preloaded-state)
        dispatch (fn [action] (swap! state #(reducer % action)))

        get-state (fn [] @state)]

    {:dispatch dispatch
     :get-state get-state}))
