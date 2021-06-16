(ns app.main.core
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            [app.redux.core :refer [use-selector use-action]]
            [app.utils.core :refer [class-names]]
            [app.ui.input :refer [Input]]
            [app.main.redux :as redux]))

(defnc Main []
  (let [address (use-selector redux/address-selector)
        on-address-change (use-action #(->> % (:target) (:value) redux/on-address-change))]

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
          "Ethernauts"))
      (d/section
        (d/div
          {:className "mb-5"}
          "Enter an Address to begin")
        ($ Input
          {:label "Address"
           :model "address"
           :value address
           :on-change on-address-change})))))
