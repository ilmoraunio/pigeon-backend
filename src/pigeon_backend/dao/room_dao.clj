(ns pigeon-backend.dao.room-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]
            [pigeon-backend.dao.dao-util :refer [initialize-query-data]]))

(s/defschema RoomModel {:name String
                        :created java.util.Date
                        :updated java.util.Date
                        :version s/Int
                        :deleted Boolean})

(s/defschema PersistedRoom {:id String
                            :name String})

(s/defschema NewRoom {:name String})

(defquery sql-room-create<! "sql/room/create.sql"
  {:connection db-spec})

(defquery sql-room-get "sql/room/get.sql"
  {:connection db-spec})

(defquery sql-room-update<! "sql/room/update.sql"
  {:connection db-spec})

(defn create! [tx room] {:pre [(s/validate NewRoom room)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-room-create<! map-args {:connection tx})) tx room))

(defn get-by [tx room]
  (let [query-data (merge (initialize-query-data RoomModel) room)]
    (execute-sql-or-handle-exception
      (fn [tx map-args]
        (sql-room-get map-args {:connection tx})) tx query-data)))

(defn update! [tx room] {:pre [(s/validate PersistedRoom room)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-room-update<! map-args {:connection tx})) tx room))

(defn delete! [])