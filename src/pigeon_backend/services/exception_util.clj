(ns pigeon-backend.services.exception-util
  (:require [schema.core :as s]
            [ring.util.http-response :refer :all]))

(s/defschema ErrorMessage
  {:error-status s/Int
   :title String
   :detail String})

(defn status-code-for [cause]
  (case cause
    :username-exists 400))

(defn return-exception-message [e]
  (let [cause (:cause (ex-data e))
        status-code (status-code-for cause)
        detail (:detail (ex-data e))]
    (bad-request {:error-status status-code
                  :title (.getMessage e)
                  :detail detail})))