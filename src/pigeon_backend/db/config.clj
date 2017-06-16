(ns pigeon-backend.db.config
  (:require [environ.core :refer [env]]
            [pigeon-backend.db.migrations :as migrations]
            [clojure.java.jdbc :as jdbc]
            [jeesql.core :refer [defqueries]]))

(def db-spec {:connection-uri (env :connection-uri)})
(defqueries "sql/ragtime_migrations.sql")