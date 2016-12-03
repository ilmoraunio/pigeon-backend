(ns pigeon-backend.routes.participant-routes
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.participant-service :as participant-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]
            [pigeon-backend.middleware :refer [wrap-auth]]
            [pigeon-backend.dao.participant-dao :as participant-dao]
            [pigeon-backend.dao.model :as model]))

(def participant-routes
  (context "/participant" []
    :middleware [wrap-auth]
    :tags ["participant"]

    (POST "/" []
      ;;:return participant-service/Model
      ;;:body [participant participant-service/New]
      :summary "Join a room (not implemented)"
      (not-implemented))
    (GET "/" []
      ;;:body [participant participant-service/QueryInput]
      :summary "Show participants in room or rooms (not implemented)"
      (not-implemented))
    (PUT "/" []
      ;;:return participant-service/Model
      ;;:body [room room-dao/Existing]
      :summary "Update alias name (not implemented)"
      (not-implemented))
    (DELETE "/" []
      ;;:return participant-service/Model
      ;;:body [participant model/Existing]
      :summary "Leave room (not implemented)"
      (not-implemented))))