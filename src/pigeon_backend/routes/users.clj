(ns pigeon-backend.routes.users
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.user-service :as user-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(def users-routes
  (context "/" []
    :tags ["message"]

    (GET "/users/:username" []
      :path-params [username :- String]
      (ok (user-service/list-users {:username username})))))
