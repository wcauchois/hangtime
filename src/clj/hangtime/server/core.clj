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
  [;"/gen/goog/base.js"
   ;"/closure-library/closure/goog/base.js"
   "/goog/base.js"
   "/scripts/jquery.min.js"
   "/scripts/bootstrap.min.js"
   "/gen/app.js"])

(def root-styles
  ["/styles/bootstrap-combined.min.css"])

(defn mime-type [path]
  (let [first-guess (URLConnection/guessContentTypeFromName path)
        extension (.substring path (.lastIndexOf path (int \.)))
        other-known-types
        {".js" "text/javascript"
         ".css" "text/css"}]
    (or first-guess (get other-known-types extension) "text/plain")))

(defn path-join [parent child]
  (.toString (File. parent child)))

(defn http-handler [channel request]
  (let [request-path (.substring (:uri request) 1) ; Eliminate the leading "/"
        public-resource (io/resource (path-join "public" request-path))
        closure-dirs ["/gen/" "/closure-library/closure/"]
        goog-resource
        (and (.startsWith request-path "goog")
             (some identity
                   (map (comp io/resource #(path-join % request-path))
                        closure-dirs)))
        resource (or public-resource goog-resource)]
    (cond
      (.isEmpty request-path)
      (enqueue channel
               {:status 200
                :headers {"Content-Type" "text/html"}
                :body (render-resource "root.mustache"
                                       {:scripts (map #(hash-map :src %) root-scripts)
                                        :styles (map #(hash-map :href %) root-styles)})
                })
      resource
      (enqueue channel
               {:status 200
                :headers {"Content-Type" (mime-type request-path)}
                :body (slurp resource)})
      :else
      (enqueue channel
               {:status 404
                :headers {"Content-Type" "text/plain"}
                :body "Not Found"}))))


(defn -main [& args]
  (mg/connect!)
  (-> "hangtime" mg/get-db mg/set-db!)
  (start-http-server http-handler {:port 3000})
  (println "Started server"))

(defn nailMain [context]
  (print "HEY WHAT"))

