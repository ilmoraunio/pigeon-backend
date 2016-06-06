(ns pigeon-backend.dao.roomgroup-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]))

(s/defschema NewRoomGroup {:room_id s/Int
                           :name String
                           :parent (s/maybe s/Int)})

(defquery sql-roomgroup-create<! "sql/roomgroup/create.sql"
  {:connection db-spec})

(defn create! [tx roomgroup] {:pre [(s/validate NewRoomGroup roomgroup)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-roomgroup-create<! map-args {:connection tx})) tx roomgroup))