(ns pigeon-backend.dao.message-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]))

(s/defschema NewMessage {:sender_roomgroup_id s/Int
                         :intended_recipient_roomgroup_id s/Int
                         :actual_recipient_roomgroup_id s/Int
                         :body String
                         :room_id s/Int
                         :time_id s/Int})

(defquery sql-message-create<! "sql/message/create.sql"
  {:connection db-spec})

(defn create! [tx message] {:pre [(s/validate NewMessage message)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-message-create<! map-args {:connection tx})) tx message))