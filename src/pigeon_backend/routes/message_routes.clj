(ns pigeon-backend.routes.message-routes
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.message-service :as message-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]
            [pigeon-backend.middleware :refer [wrap-auth]]
            [pigeon-backend.dao.model :as model]
            [pigeon-backend.util :as util]))

(def message-routes
  (context "/message" []
    :middleware [wrap-auth]
    :tags ["message"]
    :summary "Requires API token"

    (POST "/" request
      :return message-service/Model
      :body [message message-service/AddMessage]
      :summary "Add a message"
      (ok (message-service/add-message! message
                                        (util/parse-auth-key request))))
    (GET "/" request
      :return message-service/QueryResult
      :query [message-params message-service/GetMessages]
      :summary "Show message(s) in room"
      (message-service/get-messages message-params
                                    (util/parse-auth-key request)))))