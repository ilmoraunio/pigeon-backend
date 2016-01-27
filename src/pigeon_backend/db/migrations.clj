(ns pigeon-backend.db.migrations
  (:require [ragtime.jdbc :as jdbc]
            [ragtime.core :as core]
            [environ.core :refer [env]]
            [ragtime.repl :as repl]))

(defn load-config []
  {:datastore (jdbc/sql-database {:connection-uri (env :connection-uri)})
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))