(ns hangtime.server.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [monger.core :as mg]
            [monger.collection :as mc]
            [clojure.data.json :as json]
            [clojure.string :as string])
  (:use [lamina.core]
        [aleph.http]
        [clostache.parser :only [render-resource]]
        [monger.util :only [object-id]]
        [hangtime.server.router :only [defrouter]])
  (:import [java.io File]
           [java.net URLConnection]))

(def root-scripts
  ["/goog/base.js"
   "/scripts/jquery.min.js"
   "/scripts/bootstrap.min.js"
   "/app.js"
   "/scripts/requires.js"])

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

(defn http-handler [ch request]
  (let [request-path (:uri request)
        resource-dirs ["closure" "gen" "public"]
        resource
        (some identity
              (map (comp io/resource #(path-join % request-path))
                   resource-dirs))]
    (cond
      (= request-path "/")
      (enqueue ch
               {:status 200
                :headers {"Content-Type" "text/html"}
                :body (render-resource "root.mustache"
                                       {:scripts (map #(hash-map :src %) root-scripts)
                                        :styles (map #(hash-map :href %) root-styles)})
                })
      resource
      (enqueue ch
               {:status 200
                :headers {"Content-Type" (mime-type request-path)}
                :body (slurp resource)})
      :else
      (enqueue ch
               {:status 404
                :headers {"Content-Type" "text/plain"}
                :body "Not Found"}))))

(defn handle-events [params data] (prn data) (flush) :waat)
(defn handle-event [params data] (prn (:id params) data) (flush) :dawgg)

(defrouter
  api-router
  ["/events" handle-events]
  ["/event/:id" handle-event])

(defn websocket-handler [ch handshake]
  (map*
    (comp (fn [message]
            (api-router (:route message) (:data message)))
          #(json/read-str % :key-fn keyword)) ch))

(defn -main [& args]
  (mg/connect!)
  (-> "hangtime" mg/get-db mg/set-db!)
  (start-http-server http-handler {:port 3000})
  (start-http-server websocket-handler {:port 4000 :websocket true})
  (println "Started server"))
