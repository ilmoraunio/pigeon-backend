(ns pigeon-backend.db.config
  (:require [environ.core :refer [env]]
            [jeesql.core :refer [defqueries]]))

(def db-spec {:connection-uri (env :connection-uri)})
(defqueries "sql/ragtime_migrations.sql")

(extend-protocol clojure.java.jdbc/IResultSetReadColumn
  org.postgresql.jdbc.PgArray
  (result-set-read-column [pgobj rsmeta idx]
    (vec (.getArray pgobj))))