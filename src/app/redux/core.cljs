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

(defn create-store
  ([reducer preloaded-state]
   (let [state (atom preloaded-state)
         get-state (fn [] @state)

         subscriptions (atom #{})
         subscribe (fn [subscriber]
                     (invariant
                       (fn? subscriber)
                       (str "subscribe expects subscriber to be a function but found " (or subscriber "nil")))
                     (swap! subscriptions conj subscriber)
                     (fn [] (swap! subscriptions disj subscriber)))

         dispatch (fn [action]
                    (invariant
                      (and (map? action) (:type action))
                      (str "dispatch expects all actions to be a map with a type set but found " (or action "nil")))

                    (swap! state #(reducer % action))
                    (run! #(%) @subscriptions))]

     ;; dispatch INIT so that each reducer populates it's own initial state
     (dispatch {:type ::INIT})

     {:dispatch dispatch
      :get-state get-state
      :subscibe subscribe}))


  ([reducer preloaded-state enhancer]
   (invariant
     (fn? enhancer)
     (str "create-store expected enhancer to be a function but found " enhancer))

   ((enhancer create-store) reducer preloaded-state)))
