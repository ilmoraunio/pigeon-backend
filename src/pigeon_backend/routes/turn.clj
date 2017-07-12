(ns pigeon-backend.routes.turn
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.turn-service :as turn-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(def turn-routes
  (context "/turn" []
    :tags ["turn"]

    (GET "/" []
      :path-params []
      (ok (turn-service/turn-get)))

    ;;(POST "/sender/:sender/recipient/:recipient" []
    ;;  :path-params [sender :- String
    ;;                recipient :- String]
    ;;  :body-params [message :- String]
    ;;  (created (message-service/message-create! {:sender sender
    ;;                                             :recipient recipient
    ;;                                             :message message})))
    ))
