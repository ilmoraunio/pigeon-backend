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
      :return roomgroup-dao/Model
      :body [roomgroup roomgroup-dao/New]
      :summary "Create a roomgroup"
      (ok (roomgroup-service/roomgroup-create! roomgroup)))
    (GET "/" []
      :body [roomgroup roomgroup-dao/QueryInput]
      :summary "Get roomgroups with arguments defined by input schema"
      (ok (roomgroup-service/roomgroup-get-by roomgroup)))
    (DELETE "/" []
      :return roomgroup-dao/Model
      :body [roomgroup model/Existing]
      :summary "Delete roomgroup"
      (ok (roomgroup-service/roomgroup-delete! roomgroup)))))