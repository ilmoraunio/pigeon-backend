(ns pigeon-backend.dao.roomgroup-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]
            [pigeon-backend.dao.dao-util :refer [initialize-query-data]]
            [pigeon-backend.dao.model :as model]))

(def common {:room_id s/Int
             :name String
             :parent (s/maybe s/Int)
             :users_id (s/maybe s/Int)})

(s/defschema New common)

(s/defschema Existing (into model/Existing common))

(s/defschema Model (into model/Model common))

(s/defschema ServiceQueryInput {(s/optional-key :id) (s/maybe s/Int)
                                (s/optional-key :name) (s/maybe String)
                                (s/optional-key :parent) (s/maybe s/Int)
                                (s/optional-key :users_id) (s/maybe s/Int)})

(s/defschema QueryInput (s/maybe (into model/QueryInput
                                       ServiceQueryInput)))

(s/defschema QueryResult [(s/maybe Model)])

(defquery sql-roomgroup-create<! "sql/roomgroup/create.sql"
  {:connection db-spec})

(defquery sql-roomgroup-get "sql/roomgroup/get.sql"
  {:connection db-spec})

(defquery sql-roomgroup-update<! "sql/roomgroup/update.sql"
  {:connection db-spec})

(defquery sql-roomgroup-delete<! "sql/roomgroup/delete.sql"
  {:connection db-spec})

(s/defn create! [tx roomgroup :- New] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-roomgroup-create<! map-args {:connection tx})) tx roomgroup))

(s/defn get-by [tx roomgroup :- QueryInput] {:post [(s/validate QueryResult %)]}
  (let [query-data (merge (initialize-query-data Model) roomgroup)]
    (execute-sql-or-handle-exception
      (fn [tx map-args]
        (sql-roomgroup-get map-args {:connection tx})) tx query-data)))

(s/defn update! [tx roomgroup :- Existing] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-roomgroup-update<! map-args {:connection tx})) tx roomgroup))

(s/defn delete! [tx roomgroup :- model/Existing] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-roomgroup-delete<! map-args {:connection tx})) tx roomgroup))
