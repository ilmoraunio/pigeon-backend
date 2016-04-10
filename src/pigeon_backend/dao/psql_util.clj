(ns pigeon-backend.dao.psql-util
  (import java.sql.BatchUpdateException)
  (import org.postgresql.util.PSQLException))

(defn execute-sql-or-handle-exceptions [f db-spec map-args]
  (try
    (f db-spec map-args)
    (catch Exception e
      (let [message (-> e .getNextException .getMessage)]
        (when-let [findings (re-find #"username.*?already exists" message)]
          (throw
            (ex-info
              "Invalid username"
              {:cause :username-exists :detail (format "User %s already exists" (:username map-args))}))))
      (throw (.getNextException e)))))