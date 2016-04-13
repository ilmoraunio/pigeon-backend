(ns pigeon-backend.test-util
  (:require [pigeon-backend.migrations_test :refer [drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.db.migrations :as migrations]
            [cheshire.core :as cheshire]))

(defn drop-and-create-tables []
  (drop-all-tables db-spec)
  (migrations/migrate))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))