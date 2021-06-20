(ns app.ui.loading
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            [app.utils.core :refer [class-names]]
            [app.ui.pill :refer [pill]]))


(defnc loading-pulse [{:keys [children]}]
  (d/div
    {:class-name
     (class-names
       :animate-pulse)}
    ($ pill (or children "Loading"))))
