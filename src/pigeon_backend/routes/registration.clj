(ns pigeon-backend.routes.registration
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.user-service :as user-service]
            [pigeon-backend.services.exception-util :refer [ErrorMessage 
                                                            return-exception-message]]
            [robert.hooke :refer [add-hook]]))

(def registration-routes
  (context "/user" []
    :tags ["registration"]

    (PUT "/" []
      :body-params [username :- String,
                    password :- String,
                    full_name :- String]
      :summary "Creates a user account with given unique username,
                password and full name (optional)"
      (try
          (user-service/user-create!
            {:username username
             :password password
             :full_name full_name})
          (created)
        (catch clojure.lang.ExceptionInfo e
          (return-exception-message e))))))