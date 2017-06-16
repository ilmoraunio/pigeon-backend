(ns pigeon-backend.dao.user-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]
            [pigeon-backend.dao.model :as model]
            [pigeon-backend.dao.dao-util :refer [initialize-query-data]]))

(s/defschema New {:username String
                  :name String
                  :password String})

(s/defschema Existing New)

(s/defschema Model (into (dissoc model/Model :id) New))

(s/defschema ModelStripped (dissoc Model :password))

(s/defschema QueryInput (s/maybe (into model/QueryInput
                                       {(s/optional-key :username) (s/maybe String)
                                        (s/optional-key :name) (s/maybe String)
                                        (s/optional-key :password) (s/maybe String)})))

(s/defschema QueryResult [(s/maybe Model)])


(s/defschema LoginUser {:username String
                        :password String})

(defquery sql-user-create<! "sql/user/create.sql"
  {:connection db-spec})

(defquery sql-user-get "sql/user/get.sql"
  {:connection db-spec})

(s/defn create! [tx user :- New] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-user-create<! map-args {:connection tx})) tx user))

  (s/defn get-by [tx user :- QueryInput] {:post [(s/validate QueryResult %)]}
  (let [query-data (merge (initialize-query-data Model) user)]
    (execute-sql-or-handle-exception
      (fn [tx map-args]
        (sql-user-get map-args {:connection tx})) tx query-data)))