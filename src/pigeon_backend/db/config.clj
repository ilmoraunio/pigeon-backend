(ns pigeon-backend.db.config
  (:require [yesql.core :refer [defquery]]
            [environ.core :refer [env]]
            [pigeon-backend.db.migrations :as migrations]
            [clojure.java.jdbc :as jdbc]))

(def db-spec 
  {:connection-uri (env :connection-uri)})

(defquery get-table-names "sql/information_schema/get-table-names.sql"
  {:connection db-spec})

(defquery get-migrations "sql/ragtime_migrations/get-all.sql"
  {:connection db-spec})