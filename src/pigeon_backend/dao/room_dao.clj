(ns pigeon-backend.dao.room-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]
            [pigeon-backend.dao.dao-util :refer [initialize-query-data]]
            [pigeon-backend.dao.model :as model]))

(s/defschema New {:name String})

(s/defschema Existing (into model/Existing 
                            {:name String}))

(s/defschema Model (into model/Model
                         {:name String}))

(s/defschema ServiceQueryInput {(s/optional-key :id)       (s/maybe s/Int)
                                (s/optional-key :name)     (s/maybe String)
                                (s/optional-key :username) (s/maybe s/Int)})

(s/defschema QueryInput (s/maybe (into model/QueryInput
                                       ServiceQueryInput)))

(s/defschema QueryResult [(s/maybe (into Model {:joined Boolean}))])

(defquery sql-room-create<! "sql/room/create.sql"
  {:connection db-spec})

(defquery sql-room-get "sql/room/get.sql"
  {:connection db-spec})

(s/defn create! [tx room :- New] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-room-create<! map-args {:connection tx})) tx room))

(s/defn get-by [tx room :- QueryInput] {:post [(s/validate QueryResult %)]}
  (let [query-data (merge (initialize-query-data Model) room)]
    (execute-sql-or-handle-exception
      (fn [tx map-args]
        (sql-room-get map-args {:connection tx})) tx query-data)))