(ns pigeon-backend.routes.participant-routes
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.participant-service :as participant-service]
            [pigeon-backend.services.user-service :as user-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]
            [pigeon-backend.middleware :refer [wrap-auth]]
            [pigeon-backend.dao.model :as model]
            [pigeon-backend.util :as util]))

(def NewParticipant {:username String
                     :name String
                     :room_id String})

(def participant-routes
  (context "/participant" []
    :middleware [wrap-auth]
    :tags ["participant"]
    :summary "Requires API token"

    (POST "/" []
      :return participant-service/Model
      :body [participant NewParticipant]
      :summary "Join a room"

      (ok (participant-service/add-participant! participant)))
    (GET "/" request
      :query [participant {:room_id String}]
      :summary "Show participant(s) in room"
      (participant-service/get-by-room (:room_id participant)
                                       (util/parse-auth-key request)))
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