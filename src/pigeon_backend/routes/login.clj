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
      (if-let [has-access?   (user-service/check-credentials
                              {:username username
                               :password password})]
        (let [is-moderator? (user-service/is-moderator? {:username username})
              token (jws/sign {:user username
                               :is_moderator is-moderator?} (env :jws-shared-secret))]
          (ok {:session {:token token
                         :username username
                         :is_moderator is-moderator?}}))
        (unauthorized)))))