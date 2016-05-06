(ns pigeon-backend.dao.room-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]))

(s/defschema NewRoom {:name String})

(defquery sql-room-create<! "sql/room/create.sql"
  {:connection db-spec})

(defn create! [tx room] {:pre [(s/validate NewRoom room)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-room-create<! map-args {:connection tx})) tx room))

(defn get-by-name [])
(defn update! [])
(defn delete! [])