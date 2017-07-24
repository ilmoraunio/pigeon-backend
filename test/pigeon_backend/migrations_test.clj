(ns pigeon-backend.migrations_test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.db.migrations :as migrations]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec 
                                              get-table-names
                                              get-table-names-without-meta
                                              get-migrations]]
            [pigeon-backend.handler :as handler]
            [ring.server.standalone :as ring]
            [immutant.web :as immutant])
  (:import (org.postgresql.util PSQLException)))

(defn empty-all-tables [conn]
  (if-let [table-name-count (count (get-table-names-without-meta conn))]
    (if (> table-name-count 0)
      (jdbc/execute! conn [(str "TRUNCATE TABLE "
        (clojure.string/join ", " (map :table_name (get-table-names-without-meta conn)))
        " CASCADE")]))))

(defn drop-all-tables [conn]
  (if-let [table-name-count (count (get-table-names conn))]
    (if (> table-name-count 0)
      (jdbc/execute! conn [(str "DROP TABLE "
        (clojure.string/join ", " (map :table_name (get-table-names conn)))
        " CASCADE")]))))

(deftest migrations-test
  (with-state-changes [(before :facts (drop-all-tables db-spec))]
    (fact "No tables exist beforehand"
      (get-migrations db-spec) => (throws PSQLException))
    (fact "Migrate succesfully"
      (migrations/migrate)
      (map :id (get-migrations db-spec)) => (contains ["001-users"]))
    (fact "Rollback succesfully"
      (migrations/migrate)
      (let [migrations-count (get-migrations db-spec)]
        (migrations/rollback)
        migrations-count > (count (get-migrations db-spec))))
    (fact "Migrate called in main"
      (handler/-main) => irrelevant
      (provided
        (migrations/migrate) => irrelevant :times 1,
        (migrations/migrate-data) => irrelevant :times 1)
      (against-background
        (immutant/run anything anything) => irrelevant
        (reset! anything anything) => irrelevant))))