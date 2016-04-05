(ns pigeon-backend.routes.registration
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(def registration-routes
  (context "/user" []
    :tags ["registration"]

    (PUT "/" []
      :body-params [username :- String, password :- String]
      :summary "Creates a user account with given unique username and password"
      (ok))))