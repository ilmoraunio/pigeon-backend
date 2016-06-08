(ns pigeon-backend.routes.room-routes
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.room-service :as room-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]
            [pigeon-backend.middleware :refer [wrap-authentication]]
            [pigeon-backend.dao.room-dao :as user-dao]))

(def room-routes
  (context "/room" []
    ;;:middleware [wrap-authentication]
    :tags ["room"]

    (POST "/" []
      :return user-dao/Model
      :body [room user-dao/New]
      :summary "Create a room"
      (ok (room-service/room-create! room)))
    (PUT "/" []
      :return user-dao/Model
      :body [room user-dao/Existing]
      :summary "Update room"
      (ok (room-service/room-update! room)))))