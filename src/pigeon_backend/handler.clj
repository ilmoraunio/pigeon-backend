(ns pigeon-backend.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [pigeon-backend.routes.hello :refer [hello-routes]]
            [ring.server.standalone :as ring]
            [environ.core :refer [env]]
            [pigeon-backend.db.migrations :as migrations]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.reload :refer [wrap-reload]]
            [pigeon-backend.routes.registration :refer [registration-routes]])
  (:gen-class))

(defn wrap-cors-fn [handler]
  (wrap-cors handler :access-control-allow-origin [#".*"]
                     :access-control-allow-methods [:get :put :post :delete]))

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "Sample API"
                    :description "Compojure Api example"}
             :tags [{:name "api", :description "some apis"}]}}}
    hello-routes
    registration-routes))

(defn coerce-to-integer [v]
  (if (string? v)
    (Integer/parseInt v)
    v))

;; TODO: middleware to handle clojure.lang.ExceptionInfo

(defn app-fn []
  (-> #'app
      ; TODO: do not enable by default, but
      ; allow it to be enabled through app properties.
      wrap-reload 
      wrap-cors-fn))

(defn -main [& args]
  (let [port (coerce-to-integer (env :port))]
    (migrations/migrate)
    ; TODO: get production-ready server running here...
    (ring/serve (app-fn) {:port port})))