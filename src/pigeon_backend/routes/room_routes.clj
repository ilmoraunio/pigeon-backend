(ns pigeon-backend.routes.room-routes
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.room-service :as room-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]
            [pigeon-backend.middleware :refer [wrap-auth]]
            [pigeon-backend.dao.room-dao :as room-dao]
            [pigeon-backend.dao.model :as model]))

(def room-routes
  (context "/room" []
    :middleware [wrap-auth]
    :tags ["room"]

    (POST "/" []
      :return room-dao/Model
      :body [room room-dao/New]
      :summary "Create a room"
      (ok (room-service/room-create! room)))
    (GET "/" []
      :query [room room-dao/QueryInput]
      :summary "Get rooms"
      (ok (room-service/room-get-by room)))))