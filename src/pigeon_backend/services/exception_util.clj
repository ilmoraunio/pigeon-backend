(ns pigeon-backend.services.exception-util)

(defn- status-code-for [cause]
  (case cause
    :username-exists 400))

(defn execute-dao-or-handle-exception [f dto]
  (try
    (f dto)
    (catch clojure.lang.ExceptionInfo e
      (let [cause (:cause (ex-data e))
            status-code (status-code-for cause)
            detail (:detail (ex-data e))]
          {:errors
            [{:status status-code
              :title (.getMessage e)
              :detail detail}]}))))