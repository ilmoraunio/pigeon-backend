(ns pigeon-backend.routes.users
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [schema.core :as s]
            [pigeon-backend.services.user-service :as user-service]
            [pigeon-backend.middleware :refer [wrap-auth
                                               wrap-authorize]]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(def users-routes
  (context "/users" []
    :middleware [wrap-auth]
    :tags ["users"]

    (GET "/:username" []
      :path-params [username :- String]
      :middleware [(partial wrap-authorize [:params :username])]
      (ok (user-service/list-users {:username username})))))
