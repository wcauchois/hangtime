(ns hangtime.server.router
  (:use [hangtime.crossover.router-generic :only [defrouter-generic]])
  (:import [java.util.regex Pattern]))

(defn regex-compiler [s]
  (Pattern/compile s))

(defn regex-matcher [pattern s]
  (let [matcher (.matcher pattern s)]
    (and (.matches matcher)
         (map #(.group matcher %)
              (range 1 (+ 1 (.groupCount matcher)))))))

(defmacro defrouter [name & routes]
  `(defrouter-generic regex-compiler regex-matcher ~name ~@routes))
