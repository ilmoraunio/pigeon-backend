(ns pigeon-backend.services.user-service
  (:require [pigeon-backend.dao.user-dao :as user-dao]))

(defn status-code-for [cause]
  (case cause
    :username-exists 400))

(defn user-create! [dto]
  (try 
    (user-dao/create! dto)
    (catch clojure.lang.ExceptionInfo e
      (let [cause (:cause (ex-data e))
            status-code (status-code-for cause)
            detail (:detail (ex-data e))]
        {:errors
          {:status status-code
           :title (.getMessage e)
           :detail detail}}))))