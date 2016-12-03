(ns pigeon-backend.dao.participant-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]
            [pigeon-backend.dao.dao-util :refer [initialize-query-data]]
            [pigeon-backend.dao.model :as model]))

(def common {:room_id s/Int
             :name String
             :users_id (s/maybe s/Int)})

(def New common)

(def Existing (into model/Existing common))

(def Model (into model/Model common))

(def ^{:private true} QueryInput {(s/optional-key :room_id) (s/maybe s/Int)
                                  (s/optional-key :name) (s/maybe String)
                                  (s/optional-key :users_id) (s/maybe s/Int)})

(def QueryInput (s/maybe (into model/QueryInput
                               QueryInput)))

(def QueryResult [(s/maybe Model)])

(defquery sql-participant-create<! "sql/participant/create.sql"
  {:connection db-spec})

(defquery sql-participant-get "sql/participant/get.sql"
  {:connection db-spec})

(defquery sql-participant-update<! "sql/participant/update.sql"
  {:connection db-spec})

(defquery sql-participant-delete<! "sql/participant/delete.sql"
  {:connection db-spec})

(s/defn create! [tx participant :- New] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-participant-create<! map-args {:connection tx})) tx participant))

(s/defn get-by [tx participant :- QueryInput] {:post [(s/validate QueryResult %)]}
  (let [query-data (merge (initialize-query-data Model) participant)]
    (execute-sql-or-handle-exception
      (fn [tx map-args]
        (sql-participant-get map-args {:connection tx})) tx query-data)))

(s/defn update! [tx participant :- Existing] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-participant-update<! map-args {:connection tx})) tx participant))

(s/defn delete! [tx participant :- model/Existing] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-participant-delete<! map-args {:connection tx})) tx participant))
