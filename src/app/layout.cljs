(ns app.layout
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            [helix.hooks :as h]
            [redux.core :refer [use-action]]
            [app.utils.core :refer [class-names]]
            [app.redux :refer [on-mount]]
            [app.side-bar :refer [SideBar]]
            [app.main.core :refer [Main]]))

(defnc App []
  (let [on-mount (use-action on-mount)]
    (h/use-effect
      :once
      (on-mount)))

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
