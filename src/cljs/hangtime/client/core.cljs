(ns hangtime.client.core
  (:require [dommy.core :as dommy]
            [hangtime.crossover.util :as util])
  (:use-macros [dommy.macros :only [sel sel1 node deftemplate]]))

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
     [:a {:href "/#about"} "About"]
     [:strong " ⋅ "]
     [:a {:href "https://twitter.com/wcauchois"} "@wcauchois"]
     ]]])

(defn ^:export initialize []
  (dommy/append!
    (sel1 :body)
    (chrome js/location.hash)))

