(ns app.layout
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            [app.utils.core :refer [class-names]]
            [app.side-bar]))


(defnc Main []
  (d/main
    {:className
     (class-names
       :mt-12
       :text-4xl
       :flex-grow
       :px-6)}

    "Ethernauts"))

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
    ($ app.side-bar/SideBar)
    ($ Main)))
