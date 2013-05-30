(ns hangtime.client.core
  (:require [dommy.core :as dommy]
            [hangtime.crossover.util :as util])
  (:use-macros [dommy.macros :only [sel sel1 node deftemplate]]))

(deftemplate link [href text] ; XXX
  [:a {:href href} text])

(deftemplate chrome [body]
  [:div.container
   [:div.navbar.navbar-inverse
    [:div.navbar-inner
     [:div.container
      [:a.brand {:href "/"} "Hangtime"]
      [:ul.nav]]
     ]]
   body
   [:div.row
    [:div.span12 {:style {:text-align "center"}}
     (link "/#!/about" "About")
     [:strong " ⋅ "]
     [:a {:href "https://twitter.com/wcauchois"} "@wcauchois"]
     ]]])

(def *socket* (js/goog.net.WebSocket.))

(defn stringify [coll]
  (js/JSON.stringify (clj->js coll)))

(defn ^:export main []
  (dommy/append!
    (sel1 :body)
    (chrome "Hey world"))
  (.addEventListener
    *socket*
    js/goog.net.WebSocket.EventType.MESSAGE
    (fn [evt]
      (js/console.log (.-message evt))))
  (.addEventListener
    *socket*
    js/goog.net.WebSocket.EventType.OPENED
    (fn [evt]
      (.send *socket* (stringify {:route "/events" :data "bye from js"}))
      (.send *socket* (stringify {:route "/event/abc" :data "hey from js"}))))
  (.open *socket* "ws://localhost:4000"))

