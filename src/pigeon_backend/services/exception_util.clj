(ns pigeon-backend.services.exception-util
  (:require [schema.core :as s]
            [ring.util.http-response :refer :all]))

(defn- status-code-for [type]
  (case type
    :username-exists 400))

(defn- return-exception-message [e]
  (let [type (:type (ex-data e))
        status-code (status-code-for type)
        detail (:detail (ex-data e))]
    (bad-request {:error-status status-code
                  :title (.getMessage e)
                  :detail detail})))

;; public fns

(s/defschema ErrorMessage
  {:error-status s/Int
   :title String
   :detail String})

(defn handle-exception-info [^Exception e data request] 
  (return-exception-message e))