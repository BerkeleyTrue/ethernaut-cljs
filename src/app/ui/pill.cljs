(ns app.ui.pill
  (:require [helix.core :refer [defnc]]
            [helix.dom :as d]
            [app.utils.core :refer [class-names]]))

(defnc pill [{:keys [children class-name]}]
  (d/div
    {:class-name
     (class-names
       class-name
       :px-5
       :flex
       :flex-row
       :justify-center
       :items-center
       :h-11
       :text-base
       :rounded-full
       :text-red-600
       :bg-red-100
       :border-red-600
       :border
       :capitalize)}
    children))
