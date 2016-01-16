(ns pigeon-backend.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [pigeon-backend.routes.hello :refer [hello-routes]]
            [ring.server.standalone :as ring]
            [environ.core :refer [env]])
  (:gen-class))

(defapi app
  (swagger-ui)
  (swagger-docs
    {:info {:title "Pigeon-backend"
            :description "Compojure Api example"}
     :tags [{:name "hello", :description "says hello in Finnish"}]})
  (context* "/hello" []
    :tags ["hello"]
    hello-routes))

(defn coerce-to-integer [v]
  (if (string? v)
    (Integer/parseInt v)
    v))

(defn -main [& args]
  (let [port (coerce-to-integer (env :port))]
    (ring/serve app {:port port})))