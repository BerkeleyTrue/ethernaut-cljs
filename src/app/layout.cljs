(ns app.layout
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            [helix.hooks :as h]
            [redux.core :refer [use-action]]
            [app.utils.core :refer [class-names]]
            [app.provider :as provider]
            [app.side-bar :refer [SideBar]]
            [app.main.core :refer [Main]]))

(defnc App []
  (let [init-provider (use-action provider/init-provider)]
    (h/use-effect
      :once
      (init-provider)))

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
