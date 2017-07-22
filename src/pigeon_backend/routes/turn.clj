(ns pigeon-backend.routes.turn
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.turn-service :as turn-service]
            [pigeon-backend.middleware :refer [wrap-auth]]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(def turn-routes
  (context "/turn" []
    :middleware [wrap-auth]
    :tags ["turn"]

    (GET "/" []
      (ok (turn-service/turn-get)))

    (POST "/:id" []
      :path-params [id :- String]
      (ok (turn-service/turn-update! {:id id})))))
