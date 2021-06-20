(ns app.main.nav-bar.layout
  (:require [helix.core :refer [$]]
            [helix.dom :as d]
            [app.utils.core :refer [class-names]]
            [app.ui.button :refer [button]]))


(defn main []
  (d/div
    {:class-name
     (class-names
       :flex
       :flex-row
       :items-center
       :justify-center
       :mt-4
       :mb-12
       :px-2
       :w-full)}

    (d/div
      {:className
       (class-names
         :bg-gray-700
         :flex
         :flex-row
         :justify-end
         :h-16
         :px-4
         :items-center
         :rounded-xl
         :shadow-lg
         :text-white
         :w-full)}
      ($ button "connect"))))
