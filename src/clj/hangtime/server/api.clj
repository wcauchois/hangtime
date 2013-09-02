(ns hangtime.server.api
  (:require [monger.collection :as mc]
            [clojure.data.json :as json])
  (:use [hangtime.server.router :only [defrouter]]
        [lamina.core]))

(defn get-event [params data]
  (json/write-str
    (mc/find-one-as-map "events" {:id (:id params)})))

(defn websocket-handler [ch handshake]
  (map*
    (comp (fn [message]
            (api-router (:route message) (:data message)))
          #(json/read-str % :key-fn keyword)) ch))

(defn new-event [params data]
  (mc/insert "events" data))

(def *events-channel* (channel))

(defn publish-new-event [event])

(defn subscribe-events [])

(defrouter router
  ["/events/subscribe" subscribe-events]
  ["/event/:id" get-event])

(defn websocket-handler [ch handshake]
  )
