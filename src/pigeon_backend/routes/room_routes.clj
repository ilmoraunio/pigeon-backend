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
            [pigeon-backend.dao.room-dao :as user-dao]
            [pigeon-backend.dao.model :as model]))

(def room-routes
  (context "/room" []
    :middleware [wrap-auth]
    :tags ["room"]

    (POST "/" []
      :return user-dao/Model
      :body [room user-dao/New]
      :summary "Create a room"
      (ok (room-service/room-create! room)))
    (GET "/" []
      :body [room user-dao/QueryInput]
      :summary "Get rooms with arguments defined by input schema"
      (ok (room-service/room-get-by room)))
    (PUT "/" []
      :return user-dao/Model
      :body [room user-dao/Existing]
      :summary "Update room"
      (ok (room-service/room-update! room)))
    (DELETE "/" []
      :return user-dao/Model
      :body [room model/Existing]
      :summary "Delete room"
      (ok (room-service/room-delete! room)))))