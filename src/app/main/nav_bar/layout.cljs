(ns app.main.nav-bar.layout
  (:require [helix.core :refer [$]]
            [helix.dom :as d]
            ["reselect" :as reselect]
            [redux.core :refer [use-selector]]
            [app.utils.core :refer [class-names]]
            [app.ui.button :refer [button]]
            [app.ui.pill :refer [pill]]
            [app.ui.loading :refer [loading-pulse]]
            [app.provider :as provider]))

(def ^:private show-no-provider
  (.createSelector reselect
    provider/detected?-selector
    provider/run?-selector
    (fn [detected? run?] (and run? (not detected?)))))


(defn main []
  (let [show-no-provider (use-selector show-no-provider)
        show-loading (not (use-selector provider/run?-selector))
        chain-name (use-selector provider/chain-name-selector)
        show-wrong-network (use-selector
                             (comp
                               #(not
                                  (=
                                   (:rinkeby provider/chain-to-id-map)
                                   %))
                               provider/chain-id-selector))]
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
           :space-x-1
           :h-16
           :px-4
           :items-center
           :rounded-xl
           :shadow-lg
           :text-white
           :w-full)}

        (and chain-name ($ pill (str "network: " chain-name)))
        (cond
          show-loading ($ loading-pulse)
          show-no-provider ($ pill
                              {:class-name "mx-1"}
                              "no metamask provider detected")
          show-wrong-network ($ pill
                                "warning! wrong network.")

          :else ($ button "connect"))))))
