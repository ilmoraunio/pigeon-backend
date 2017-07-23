(ns pigeon-backend.db.config
  (:require [environ.core :refer [env]]
            [jeesql.core :refer [defqueries]]
            [cheshire.core :refer [parse-string]])
  (:import (org.postgresql.jdbc PgArray)
           (org.postgresql.util PGobject)))

(def db-spec {:connection-uri (env :connection-uri)})
(defqueries "sql/ragtime_migrations.sql")

(extend-protocol clojure.java.jdbc/IResultSetReadColumn
  PgArray
  (result-set-read-column [pgobj rsmeta idx]
    (vec (.getArray pgobj))))

(extend-protocol clojure.java.jdbc/IResultSetReadColumn
  PGobject
  (result-set-read-column [pgobj rsmeta idx]
    (parse-string (.getValue pgobj))))