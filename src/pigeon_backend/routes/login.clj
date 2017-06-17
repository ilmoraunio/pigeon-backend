(ns pigeon-backend.routes.login
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.user-service :as user-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(def login-routes
  (context "/session" []
    :tags ["login"]

    (POST "/" []
      :body-params [username :- String,
                    password :- String]
      :summary "Logs user in iff username and password match persisted user in the database. Sets token to http cookie."
      (if-let [has-access? (user-service/check-credentials
                            {:username username
                             :password password})]
        (let [token (jws/sign {:user username} (env :jws-shared-secret))]
          (ok {:session {:token token
                         :username username}}))
        (unauthorized)))))

(def login-token-route
  (context "/token" []
    :tags ["login"]

    (POST "/" []
      :body-params [email :- String])
      ;; todo: send sms
      (not-implemented)))

(def login-route
  (context "/login" []
    :tags ["login"]

    (GET "/" []
      :query-params [name :- String]
      ;; todo: verify magic link
      (not-implemented))))