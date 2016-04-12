(ns pigeon-backend.routes.registration
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.user-service :as user-service]
            [pigeon-backend.services.exception-util :refer [ErrorMessage]]))

(def registration-routes
  (context "/user" []
    :tags ["registration"]

    (PUT "/" []
      :body-params [username :- String,
                    password :- String,
                    full_name :- String]
      :summary "Creates a user account with given unique username,
                password and full name (optional)"
      (let [return-value (user-service/user-create! 
                           {:username username
                            :password password
                            :full_name full_name})]
        (created)))))