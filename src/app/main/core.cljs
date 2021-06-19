(ns app.main.core
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            [helix.hooks :as h]
            [redux.core :refer [use-selector use-action]]
            ["@metamask/detect-provider" :as detect-provider]
            [app.utils.core :refer [class-names]]
            [app.ui.input :refer [Input]]
            [app.main.redux :as redux]))

(defn use-provider []
  (let [[provider set-provider] (h/use-state nil)]
    (h/use-effect
      []
      (let [pprovider (detect-provider)]
        (->
          pprovider
          (.then set-provider)
          (.catch #(print "Error couldn't find provider: " %)))))

    provider))

(defnc Main []
  (let [provider (use-provider)
        address (use-selector redux/address-selector)
        valid? (use-selector redux/valid?-selector)
        on-address-change (use-action #(-> % (.. -target -value) redux/on-address-change))]

    (d/main
      {:className
       (class-names
         :mt-12
         :flex-grow
         :px-6)}

      (d/header
        (d/h1
          {:className
           (class-names
             :text-4xl)}
          "Ethernaut"))
      (d/section
        (d/div
          {:className "mb-5"}
          "Enter an Address to begin")
        ($ Input
          {:label "Address"
           :model "address"
           :value address
           :invalid (not valid?)
           :on-change on-address-change})))))
