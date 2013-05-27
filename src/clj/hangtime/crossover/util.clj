(ns hangtime.crossover.util
  (:require [clojure.string :as string]))

(defn path-regex [path-spec]
  (string/join "/" (map (fn [part]
                          (if (.startsWith part ":") "(.*?)" part))
                        (string/split path-spec #"/"))))

