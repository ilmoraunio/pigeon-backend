(ns pigeon-backend.dao.time-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]))

(s/defschema NewTime {:room_id s/Int
                      :name String
                      :sequence_order s/Int})

(defquery sql-time-create<! "sql/time/create.sql"
  {:connection db-spec})

(defn create! [tx time] {:pre [(s/validate NewTime time)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-time-create<! map-args {:connection tx})) tx time))