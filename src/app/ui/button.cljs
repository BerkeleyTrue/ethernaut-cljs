(ns app.ui.button
  (:require [helix.dom :as d]
            [app.utils.core :refer [class-names]]))

(defn button [props]
  (let [children (.-children props)]

    (d/button
      {:class-name
       (class-names
         :px-6
         :h-11
         :text-base
         :text-white
         :transition
         :ease-in
         :duration-200
         :font-semibold
         :shadow-md
         :flex
         :flex-row
         :items-center
         :justify-center
         :border-2
         :border-white
         :rounded-xl
         :uppercase
         :hover:text-gray-900
         :hover:bg-white
         :focus:outline-none
         :focus:ring-2
         :focus:ring-offset-4)}
      (or children "Button"))))
