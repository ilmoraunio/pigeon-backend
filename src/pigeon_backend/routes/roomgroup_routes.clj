(ns pigeon-backend.routes.roomgroup-routes
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.roomgroup-service :as roomgroup-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]
            [pigeon-backend.middleware :refer [wrap-auth]]
            [pigeon-backend.dao.roomgroup-dao :as roomgroup-dao]
            [pigeon-backend.dao.model :as model]))

(def roomgroup-routes
  (context "/roomgroup" []
    :middleware [wrap-auth]
    :tags ["roomgroup"]

    (POST "/" []
      ;;:return roomgroup-service/Model
      ;;:body [roomgroup roomgroup-service/New]
      :summary "Join a room"
      (not-implemented))
    (GET "/" []
      ;;:body [roomgroup roomgroup-service/QueryInput]
      :summary "Show participants in room or rooms"
      (not-implemented))
    (PUT "/" []
      ;;:return roomgroup-service/Model
      ;;:body [room room-dao/Existing]
      :summary "Update alias name"
      (not-implemented))
    (DELETE "/" []
      ;;:return roomgroup-service/Model
      ;;:body [roomgroup model/Existing]
      :summary "Leave room"
      (not-implemented))))