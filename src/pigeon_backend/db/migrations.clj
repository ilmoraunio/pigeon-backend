(ns pigeon-backend.db.migrations
  (:require [ragtime.jdbc :as jdbc]
            [ragtime.core :as core]
            [environ.core :refer [env]]
            [ragtime.repl :as repl]))

(defn load-config []
  {:datastore (jdbc/sql-database {:connection-uri (env :connection-uri)})
   :migrations (jdbc/load-resources "migrations")})

(defn load-data-config []
  {:datastore (jdbc/sql-database {:connection-uri (env :connection-uri)}
                {:migrations-table "ragtime_data_migrations"})
   :migrations (jdbc/load-resources "data-migrations")})

(defn load-data-extra-config []
  {:datastore (jdbc/sql-database {:connection-uri (env :connection-uri)}
                {:migrations-table "ragtime_data_extra_migrations"})
   :migrations (jdbc/load-resources "data-migrations-extra")})

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))

(defn migrate-data []
  (repl/migrate (load-data-config)))

(defn rollback-data []
  (repl/rollback (load-data-config)))

(defn migrate-data-extra []
  (repl/migrate (load-data-extra-config)))

(defn rollback-data-extra []
  (repl/rollback (load-data-extra-config)))