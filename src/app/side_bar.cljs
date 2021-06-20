(ns app.side-bar
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            [app.utils.core :refer [class-names]]))

(defnc NavItem []
  (d/a
    {:className
     (class-names
      :hover:text-gray-800
      :hover:bg-gray-100
      :flex
      :items-center
      :p-2
      :my-6
      :transition-colors
      :dark:hover:text-white
      :dark:hover:bg-gray-600
      :duration-200
      :text-gray-600
      :dark:text-gray-400
      :rounded-lg)}
    (d/span
      {:className
       (class-names
         :mx-4
         :text-lg
         :font-normal)}
      "Coin Flip")
    (d/span
      {:className
       (class-names
         :flex-grow
         :text-right)})))




(defnc SideBar []
  (d/aside
    {:className
     (class-names
       :bg-gray-800
       :flex-row
       :justify-around
       :h-40
       :w-100
       :sm:h-screen
       :sm:w-44
       :sm:flex-col
       :sm:justify-around)}

    (d/nav
      {:className
       (class-names
         :mt-10
         :px-6)}

      ($ NavItem))))
