(ns app.layout
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            [app.utils.core :refer [class-names]]
            [app.side-bar :refer [SideBar]]
            [app.main.core :refer [Main]]))

(defnc App []
  (d/div
    {:className
     (class-names
       :bg-gray-200
       :flex
       :flex-nowrap
       :flex-col
       :sm:flex-row
       :h-screen
       :overflow-hidden
       :relative
       :w-screen)}
    ($ SideBar)
    ($ Main)))
