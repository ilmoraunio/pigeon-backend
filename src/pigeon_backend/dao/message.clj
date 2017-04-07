(ns pigeon-backend.dao.message
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]
            [pigeon-backend.dao.dao-util :refer [initialize-query-data]]
            [pigeon-backend.dao.model :as model]))

(def common {:room_id String
             :sender String
             :recipient String
             :message String})
(def New common)
(def Model (into model/Model common))
(def GetMessages {:room_id   String
                  :sender    String
                  :recipient String})
(def QueryResult [(s/maybe (into Model {:is_from_sender Boolean
                                        :sender_name String}))])

(defquery sql-message-create<! "sql/message/create.sql"
  {:connection db-spec})

(defquery sql-message-get-messages "sql/message/get-messages.sql"
  {:connection db-spec})

(s/defn create! [tx message :- New] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-message-create<! map-args {:connection tx})) tx message))

(s/defn get-messages [tx message :- GetMessages] {:post [(s/validate QueryResult %)]}
  (let [query-data (merge (initialize-query-data Model) message)]
    (execute-sql-or-handle-exception
      (fn [tx map-args]
        (sql-message-get-messages map-args {:connection tx})) tx query-data)))