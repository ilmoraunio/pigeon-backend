(ns pigeon-backend.routes.registration
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

; TODO: move to dao layer
(s/defschema UserModel
  {:id s/Int 
   :username String
   :password String
   :deleted Boolean})

(def registration-routes
  (context "/user" []
    :tags ["registration"]

    (PUT "/" []
      :query-params [username :- String, password :- String]
      :summary "Creates a user account with given unique username and password"
      (ok))))