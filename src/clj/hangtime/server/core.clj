(ns hangtime.server.core
  (:gen-class)
  (:use
    [lamina.core]
    [aleph.http]
    [clostache.parser]
    [compojure.core])
  (:require [compojure.route :as route]))

(def scripts
  ["/gen/goog/base.js"
   "/scripts/jquery.min.js"
   "/scripts/bootstrap.min.js"
   "/gen/app.js"])

(def styles
  ["/styles/bootstrap-combined.min.css"])

(defn index [channel request]
  (enqueue channel
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (render-resource "root.mustache"
                            {:scripts (map #(hash-map :src %) scripts)
                             :styles (map #(hash-map :href %) styles)})
     }))

(defroutes my-routes
  (GET "/" [] (wrap-aleph-handler index))
  (route/resources "/")
  (route/not-found "Not found"))

(defn -main [& args]
  (start-http-server (wrap-ring-handler my-routes) {:port 3000}))
