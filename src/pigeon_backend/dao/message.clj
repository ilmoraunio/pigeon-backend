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

(defquery sql-participant-create<! "sql/message/create.sql"
  {:connection db-spec})

(s/defn create! [tx participant :- New] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-participant-create<! map-args {:connection tx})) tx participant))