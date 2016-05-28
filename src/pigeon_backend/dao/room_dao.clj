(ns pigeon-backend.dao.room-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]
            [pigeon-backend.dao.dao-util :refer [initialize-query-data]]
            [pigeon-backend.dao.model :as model]))

(s/defschema Input (into model/Input
                         {(s/optional-key :name) (s/maybe String)}))

(s/defschema Model (into model/Model
                         {:name String}))

(s/defschema QueryResult [(s/maybe Model)])

(defquery sql-room-create<! "sql/room/create.sql"
  {:connection db-spec})

(defquery sql-room-get "sql/room/get.sql"
  {:connection db-spec})

(defquery sql-room-update<! "sql/room/update.sql"
  {:connection db-spec})

(defquery sql-room-delete<! "sql/room/delete.sql"
  {:connection db-spec})

(s/defn create! [tx room :- Input] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-room-create<! map-args {:connection tx})) tx room))

(s/defn get-by [tx room :- (s/maybe Input)] {:post [(s/validate QueryResult %)]}
  (let [query-data (merge (initialize-query-data Model) room)]
    (execute-sql-or-handle-exception
      (fn [tx map-args]
        (sql-room-get map-args {:connection tx})) tx query-data)))

(s/defn update! [tx room :- Input] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-room-update<! map-args {:connection tx})) tx room))

(s/defn delete! [tx room :- Input] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-room-delete<! map-args {:connection tx})) tx room))
