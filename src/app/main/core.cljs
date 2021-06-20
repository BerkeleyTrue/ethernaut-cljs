(ns app.main.core
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            [redux.core :refer [use-selector use-action]]
            [app.utils.core :refer [class-names]]
            [app.ui.input :refer [Input]]
            [app.main.nav-bar.core :as navbar]
            [app.main.redux :as redux]))

(defnc Main []
  (let [address (use-selector redux/address-selector)
        valid? (use-selector redux/valid?-selector)
        on-address-change (use-action #(-> % (.. -target -value) redux/on-address-change))]

    (d/main
      {:className
       (class-names
         :flex-grow)}

      ($ navbar/main)
      (d/article
        {:className "mx-6"}
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
             :on-change on-address-change}))))))
