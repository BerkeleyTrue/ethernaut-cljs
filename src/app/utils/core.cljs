(ns app.utils.core
  (:require [clojure.string :as str]
            [cljs-bean.core :refer [bean]]
            ["invariant" :as invariant]))

(defn over-args [f & transforms]
  (invariant
    (every? #(fn? %) transforms)
    "over-args expects every transform to be a function but found %s" transforms)
  (fn [& args]
    (apply f (map #(%1 %2)
               (concat transforms (repeat identity))
               args))))

(comment
  (fn? (over-args * inc))
  (= ((over-args identity inc) 1) 2)
  (= ((over-args * inc #(* % 2)) 1 2) 8)
  (= ((over-args vector #(* % 2) #(* % %)) 9 3) [81 6])
  (try
    (over-args inc 1)
    (catch js/Error e
      (instance? js/Error e))))

(defn wrap-js-args [f]
  (over-args f #(and % (bean %))))

(comment
  ((wrap-js-args identity) #js {"chainId" inc}))

(defn class-names [& args]
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
