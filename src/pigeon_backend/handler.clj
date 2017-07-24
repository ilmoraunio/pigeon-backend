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
            [pigeon-backend.routes.login :refer [login-routes]]
            [pigeon-backend.routes.message :refer [message-routes message-attempt-routes]]
            [pigeon-backend.routes.users :refer [users-routes]]
            [pigeon-backend.routes.turn :refer [turn-routes]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [buddy.sign.jws :as jws]
            [immutant.web :as immutant]
            [immutant.web.middleware :refer [wrap-websocket]]
            [pigeon-backend.websocket :refer [channels async-send! ws-app]])
  (:gen-class))

(def async-keepalive (atom nil))

(defn wrap-cors [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
        (assoc-in [:headers "Access-Control-Allow-Origin"] "*")
        (assoc-in [:headers "Access-Control-Allow-Methods"] "GET,PUT,POST,PATCH,DELETE,OPTIONS")
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
      message-routes
      users-routes
      turn-routes
      message-attempt-routes
      (GET "/ws/:username" [username] #(ws-app %1 username)))))

(defn coerce-to-integer [v]
  (if (string? v)
    (Integer/parseInt v)
    v))

(defn set-interval [callback ms]
  (future (while true
            (do (Thread/sleep ms)
                (callback)))))

(defn app-with-middleware
  ([] (-> #'app
          ; TODO: do not enable by default, but
          ; allow it to be enabled through app properties.
          wrap-reload
          wrap-cors
          wrap-cookies)))

(defn -main [& args]
  (migrations/migrate)
  (migrations/migrate-data)
  (let [port (coerce-to-integer (env :port 8080))
        host                    (env :host "localhost")]
    (immutant/run (app-with-middleware) {:port port
                                         :host host})
    (reset! async-keepalive (set-interval #(async-send! @channels "ping") 15000))))