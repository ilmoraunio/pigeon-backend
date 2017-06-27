(ns pigeon-backend.routes.message
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.user-service :as user-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(def message-routes
  (context "/message" []
    :tags ["message"]

    (GET "/sender/:sender/recipient/:recipient" []
      :path-params [sender :- String,
                    recipient :- String])

    (POST "/sender/:sender/recipient/:recipient" []
      :path-params [sender :- String
                    recipient :- String]
      :body-params [message :- String])))
