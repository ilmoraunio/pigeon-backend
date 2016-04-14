(ns pigeon-backend.routes.login
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.user-service :as user-service]))

(def login-routes
  (context "/user" []
    :tags ["registration"]

    (POST "/login" []
      :body-params [username :- String,
                    password :- String]
      :summary "Logs user in iff username and password match the database"
      (if-let [has-access? (user-service/check-credentials
                            {:username username
                             :password password})]
        (ok)
        (unauthorized)))))