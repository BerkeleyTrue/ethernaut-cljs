(ns app.redux.dev-tool-ext)

(defonce dev-tool-connection (atom nil))

(defn dev-tools-enhancer
  "adds redux devtool capabilities if extension is present"
  []
  (fn [create-store]
    (fn [reducer preloaded-state]
      (let [store (create-store reducer preloaded-state)
            dev-tool-ext (.-__REDUX_DEVTOOLS_EXTENSION__ js/window)]
        (if (not dev-tool-ext) store
          (let [dev-tool (if @dev-tool-connection @dev-tool-connection
                           (reset! dev-tool-connection (.connect dev-tool-ext (clj->js {}))))

                get-js-state (comp clj->js (:get-state store))

                dispatch (fn [action]
                           (let [res ((:dispatch store) action)]
                             (.send dev-tool (clj->js action) (get-js-state))
                             res))]

            (.init dev-tool (get-js-state))

            {:dispatch dispatch
             :get-state (:get-state store)}))))))
