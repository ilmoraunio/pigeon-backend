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

(defn handle-port-env [v]
  (cond (and (string? v)
             ((complement empty?) v))
        (Integer/parseInt (env :port))
        (integer? v)
        v))

(defn -main [& args]
  (let [port (handle-port-env (env :port))]
    (ring/serve app {:port port})))