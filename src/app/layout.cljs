(ns app.layout
  (:require [helix.core :refer [defnc]]
            [helix.dom :as d]))


(defnc App []
  (d/div {:className "box-border container"}
    "Hello World"))
