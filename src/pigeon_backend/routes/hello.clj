(ns pigeon-backend.routes.hello
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(s/defschema Message {:message String})

(def hello-routes
  (context "/hello" []
    :tags ["hello"]

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