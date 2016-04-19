(ns pigeon-backend.routes.hello
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [pigeon-backend.middleware :refer [wrap-authentication]]))

(s/defschema Message {:message String})

(def hello-routes
  (context "/hello" []
    :middleware [wrap-authentication]
    :tags ["test"]

    (GET "/" []
        :return Message
        :query-params [name :- String]
        :summary "say hello"
        (ok {:message (str "Terve, " name)}))

    (GET "/en" []
        :return Message
        :query-params [name :- String]
        :summary "say hello in English"
        (ok {:message (str "Hello, " name)}))))