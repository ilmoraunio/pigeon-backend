(ns pigeon-backend.services.user-service
  (:require [pigeon-backend.dao.user-dao :as user-dao]))

(defn status-code-for [cause]
  (case cause
    :username-exists 400))

(defn user-create! [dto]
  (try 
    (user-dao/create! dto)
    (catch clojure.lang.ExceptionInfo e
      (let [status-code (status-code-for cause)
            cause (:cause (ex-data e))
            detail (:detail (ex-data e))]
        {:status status-code
         :title (.getMessage e)
         :detail detail}))))