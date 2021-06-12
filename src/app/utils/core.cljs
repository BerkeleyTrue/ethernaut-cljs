(ns app.utils.core
  (:require [clojure.string :as str]))

(defn ^:export class-names [& args]
  (str/join " "
    (mapv name
      (reduce
        (fn [arr arg]
          (cond
            (or (string? arg)
                (symbol? arg)
                (keyword? arg)) (conj arr arg)
            (vector? arg) (vec (concat arr arg))
            (map? arg) (vec (concat arr
                              (reduce-kv
                                (fn [map-arr key value]
                                  (if (true? value)
                                    (conj map-arr key)
                                    map-arr)) [] arg)))
            :else arr))
        []
        args))))
