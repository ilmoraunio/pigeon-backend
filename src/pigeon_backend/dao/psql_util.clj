(ns pigeon-backend.dao.psql-util
  (import java.sql.BatchUpdateException)
  (import org.postgresql.util.PSQLException))

(defn execute-sql-or-handle-exception [f db-spec map-args]
  (try
    (f db-spec map-args)
    (catch Exception e
      (let [message (-> e .getMessage)]
        (when-let [findings (re-find #"username.*?already exists" message)]
          (throw
            (ex-info
              "Duplicate username"
              {:type :username-exists
               :cause (format "User %s already exists" (:username map-args))})))
        (when-let [findings (re-find #"name.*?already exists" message)]
          (throw
            (ex-info
              "Duplicate name"
              {:type :duplicate-name
               :cause (format "Name %s already exists" (:name map-args))}))))
      (throw (.getNextException e)))))