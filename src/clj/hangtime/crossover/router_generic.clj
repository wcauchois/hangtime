(ns hangtime.crossover.router-generic
  (:require [clojure.string :as string]))

(defn path-regex [path-spec]
  (string/join "/" (map (fn [part]
                          (if (.startsWith part ":") "(.*?)" part))
                        (string/split path-spec #"/"))))

(defn extract-params [path-spec]
  (->> (string/split path-spec #"/")
    (filter #(.startsWith % ":"))
    (map (comp keyword #(.substring % 1)))))

(defn router [matcher compiled-routes]
  (fn [path & more]
    (->> compiled-routes
      (map (fn [[regex param-names handler]]
             (let [matches (matcher regex path)]
               (if matches
                 (let [params (zipmap param-names matches)]
                   (or (apply handler (cons params more)) true))))))
      (some identity))))

(defmacro defrouter-generic [compiler matcher name & routes]
  `(def ~name
     (let [compiled-routes#
           (map (fn [[path-spec# handler#]]
                  [(~compiler (path-regex path-spec#))
                   (extract-params path-spec#)
                   handler#]) (vector ~@routes))]
       (router ~matcher compiled-routes#))))
