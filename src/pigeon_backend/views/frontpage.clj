(ns pigeon-backend.views.frontpage
  (:require [hiccup.page :as h]
            [pigeon-backend.views.layout :as layout]))

(defn index []
  (layout/common 
    "frontpage"
    [:div [:p [:a {:href "/Registration"} "Sign up"]]
          [:p [:a {:href "/login"} "Login"]]]))