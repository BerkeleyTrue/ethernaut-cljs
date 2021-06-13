(ns app.main.core
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            [app.utils.core :refer [class-names]]
            [app.ui.input :refer [Input]]))


(defnc Main []
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
         :model "address"}))))
