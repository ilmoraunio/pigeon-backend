(ns pigeon-backend.routes.message
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.message-service :as message-service]
            [pigeon-backend.middleware :refer [wrap-auth
                                               wrap-authorize
                                               wrap-auth-moderator]]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(def message-routes
  (context "/message" []
    :middleware [wrap-auth]
    :tags ["message"]

    (GET "/sender/:sender/recipient/:recipient" []
      :path-params [sender :- String,
                    recipient :- String]
      :middleware [(partial wrap-authorize [:params :sender])]
      (ok (message-service/message-get {:sender sender
                                        :recipient recipient})))

    (POST "/sender/:sender/recipient/:recipient" []
      :path-params [sender :- String
                    recipient :- String]
      :body-params [message :- String]
      :middleware [(partial wrap-authorize [:params :sender])]
      (created (message-service/message-create! {:sender sender
                                                 :recipient recipient
                                                 :message message})))

    (GET "/" []
      :middleware [wrap-auth-moderator]
      (ok (message-service/moderator-messages-get)))

    (DELETE "/:id" []
      :path-params [id :- s/Int]
      :middleware [wrap-auth-moderator]
      (do (message-service/message-delete! {:id id})
          (no-content)))

    (PATCH "/:id" []
      :path-params [id :- s/Int]
      :middleware [wrap-auth-moderator]
      (do (message-service/message-undelete! {:id id})
          (no-content)))))

(def message-attempt-routes
  (context "/message_attempt" []
    :middleware [wrap-auth
                 wrap-auth-moderator]
    :tags ["message"]

    (DELETE "/:id" []
      :path-params [id :- s/Int]
      (do (message-service/message-attempt-delete! {:id id})
          (no-content)))

    (PATCH "/:id" []
      :path-params [id :- s/Int]
      (do (message-service/message-attempt-undelete! {:id id})
          (no-content)))))

(def message-character-limit-route
  (context "/message_character_limit" []
    :middleware [wrap-auth]
    :tags ["message"]
    (GET "/" []
      (ok {:message-character-limit (Integer. (env :message-character-limit))}))))