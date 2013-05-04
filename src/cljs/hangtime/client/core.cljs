(ns hangtime.client.core
  (:require [dommy.core :as dommy])
  (:use-macros [dommy.macros :only [sel sel1 node deftemplate]]))

(deftemplate chrome [body]
  [:div {:class "container"}
   [:div {:class "navbar navbar-inverse"}
    [:div {:class "navbar-inner"}
     [:div {:class "container"}
      [:a {:class "brand" :href "/"} "Hangtime"]
      [:ul {:class "nav"}]]
     ]]
   body])

(defn ^:export initialize []
  (dommy/append!
    (sel1 :body)
    (chrome "hello world")))

