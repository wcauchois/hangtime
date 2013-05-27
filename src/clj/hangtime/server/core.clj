(ns hangtime.server.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [monger.core :as mg]
            [monger.collection :as mc])
  (:use [lamina.core]
        [aleph.http]
        [clostache.parser :only [render-resource]])
  (:import [java.io File]
           [java.net URLConnection]))

(def root-scripts
  ["/gen/goog/base.js"
   "/scripts/jquery.min.js"
   "/scripts/bootstrap.min.js"
   "/gen/app.js"])

(def root-styles
  ["/styles/bootstrap-combined.min.css"])

(defn http-handler [channel request]
  (let [path (.substring (:uri request) 1)
        resource-path (io/resource (.toString (File. "public" path)))]
    (cond
      (.isEmpty path)
      (enqueue channel
               {:status 200
                :headers {"Content-Type" "text/html"}
                :body (render-resource "root.mustache"
                                       {:scripts (map #(hash-map :src %) root-scripts)
                                        :styles (map #(hash-map :href %) root-styles)})
                })
      (not (nil? resource-path))
      (enqueue channel
               {:status 200
                :headers {"Content-Type"
                          (URLConnection/guessContentTypeFromName path)}
                :body (slurp resource-path)})
      :else
      (enqueue channel
               {:status 404
                :headers {"Content-Type" "text/plain"}
                :body "Not Found"}))))

(defn -main [& args]
  (mg/connect!)
  (-> "hangtime" mg/get-db mg/set-db!)
  (start-http-server http-handler {:port 3000}))

