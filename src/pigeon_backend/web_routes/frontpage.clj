(ns pigeon-backend.web-routes.frontpage
  (:require [compojure.core :refer [defroutes GET POST]]
            [pigeon-backend.views.frontpage :as view]))

(defn index []
  (view/index))

(defn routes []
  (GET "/" [] (index)))