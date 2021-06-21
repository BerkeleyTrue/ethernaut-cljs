(ns app.main.nav-bar.redux
  (:require [redux.verticals :as verts]
            [app.provider :as provider]))


(def click-on-connect (verts/compose-actions
                        (verts/create-action ::click-on-connect)
                        provider/connect-wallet))
