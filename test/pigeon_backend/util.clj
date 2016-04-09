(ns pigeon-backend.util
  (:require [pigeon-backend.migrations_test :refer [drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.db.migrations :as migrations]))

(defn drop-and-create-tables []
  (drop-all-tables db-spec)
  (migrations/migrate))