(ns app.ui.input
  (:require [clojure.string :as str]
            [helix.core :refer [defnc]]
            [helix.dom :as d]
            [helix.hooks :refer [use-callback use-state use-ref]]
            [app.utils.core :refer [class-names]]))

(defn use-change [iv-on-change on-change]
  (use-callback
    [on-change iv-on-change]
    (fn [e]
      (let [v e.target.value]
        (when (fn? on-change)
          (on-change e))
        (iv-on-change v)))))

(defnc Input [{:keys
               [label
                model
                value
                on-change
                invalid
                disabled]}]


  (let [[iv iv-on-change] (use-state value)
        controlled? (use-ref (string? value))
        handle-change (use-change iv-on-change on-change)
        input-classes (class-names
                        :border
                        :rounded
                        (cond
                          invalid :border-red-600
                          :else :border-gray-400)
                        :text-lg
                        :leading-normal
                        :h-11
                        :py-3
                        :pr-2.5
                        :pl-0.5
                        :w-full)
        label-classes (class-names
                        :absolute
                        :-top-4
                        :left-1
                        :text-xs
                        (cond
                          invalid :text-red-600
                          disabled :text-gray-200
                          (not (str/blank? (if @controlled? value iv))) :text-blue-500
                          :else :text-gray-500))]

    (d/div
      {:className
       (class-names
         :relative
         :my-4
         {:opacity-50 disabled
          :pointer-events-none disabled}
         :h-11)}

      (d/label
        {:htmlFor model}
        (d/input
          {:id model
           :value (if @controlled? value iv)
           :on-change handle-change
           :className input-classes})

        (d/span
          {:className label-classes}
          label)))))
