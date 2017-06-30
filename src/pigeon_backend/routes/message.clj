(ns pigeon-backend.routes.message
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.message-service :as message-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(def message-routes
  (context "/message" []
    :tags ["message"]

    (GET "/sender/:sender/recipient/:recipient" []
      :path-params [sender :- String,
                    recipient :- String]
      (ok (message-service/message-get {:sender sender
                                        :recipient recipient})))

    (POST "/sender/:sender/recipient/:recipient" []
      :path-params [sender :- String
                    recipient :- String]
      :body-params [message :- String]
      (ok (message-service/message-create! {:sender sender
                                            :recipient recipient
                                            :message message})))))
