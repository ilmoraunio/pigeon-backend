(ns pigeon-backend.handler
  (:require [compojure.api.sweet :refer :all]
            [compojure.route :as route]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [pigeon-backend.routes.hello :refer [hello-routes]]
            [ring.server.standalone :as ring]
            [environ.core :refer [env]]
            [pigeon-backend.db.migrations :as migrations]
            [ring.middleware.reload :refer [wrap-reload]]
            [pigeon-backend.services.exception-util :refer [handle-exception-info]]
            [pigeon-backend.routes.login :refer [login-routes login-token-route login-route]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [buddy.sign.jws :as jws])
  (:gen-class))

(defn wrap-cors [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
          (assoc-in [:headers "Access-Control-Allow-Origin"] "*")
          (assoc-in [:headers "Access-Control-Allow-Methods"] "GET,PUT,POST,DELETE,OPTIONS")
          (assoc-in [:headers "Access-Control-Allow-Headers"] "X-Requested-With,Content-Type,Cache-Control,Authorization,Access-Control-Request-Headers,Accept")))))

(def app
  (api
    {:swagger
     {:ui "/api"
      :spec "/swagger.json"
      :data {:info {:title "Sample API"
                    :description "Compojure Api example"}
             :tags [{:name "api", :description "some apis"}]
             :securityDefinitions {"Bearer" {:type "apiKey" :name "Authorization" :in "header"}}}}
     ;; TODO: exception handler for returning schema validation errors
     :exceptions {:handlers {:compojure.api.exception/default handle-exception-info}}}
    (context "/api/v0" []
          hello-routes
          login-routes
          login-token-route
          login-route)))

(defn coerce-to-integer [v]
  (if (string? v)
    (Integer/parseInt v)
    v))

(defn app-with-middleware
  ([] (-> #'app
          ; TODO: do not enable by default, but
          ; allow it to be enabled through app properties.
          wrap-reload
          wrap-cors
          wrap-cookies)))

(defn -main [& args]
  (let [port (coerce-to-integer (env :port))]
    (migrations/migrate)
    ; TODO: get production-ready server running here...
    (ring/serve (app-with-middleware) {:port port})))