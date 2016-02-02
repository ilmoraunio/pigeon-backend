(ns pigeon-backend.migrations_test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.db.migrations :as migrations]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec 
                                              get-table-names 
                                              get-migrations]]
            [pigeon-backend.handler :as handler]
            [ring.server.standalone :as ring])
  (:import (org.postgresql.util PSQLException)))

(defn drop-all-tables [conn]
  (if-let [table-name-count (count (get-table-names))]
    (if (> table-name-count 0)
      (jdbc/execute! conn [(str "DROP TABLE "
        (clojure.string/join ", " (map :table_name (get-table-names)))
        " CASCADE")]))))

(deftest migrations-test
  (with-state-changes [(before :facts (drop-all-tables db-spec))]
    (fact "No tables exist beforehand"
      (get-migrations) => (throws PSQLException))
    (fact "Migrate succesfully"
      (migrations/migrate)
      (map :id (get-migrations)) => (contains ["001-users"]))
    (fact "Rollback succesfully"
      (migrations/migrate)
      (migrations/rollback)
      (count (get-migrations)) => 0)
    (fact "Migrate called in main"
      (handler/-main) => irrelevant
      (provided
        (migrations/migrate) => irrelevant :times 1)
      (against-background
        (ring/serve anything anything) => irrelevant))))