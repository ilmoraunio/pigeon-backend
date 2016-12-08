(ns pigeon-backend.dao.user-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]
            [pigeon-backend.dao.model :as model]
            [pigeon-backend.dao.dao-util :refer [initialize-query-data]]))

(s/defschema New {:username String
                  :full_name String
                  :password String})

(s/defschema Existing (into model/Existing New))

(s/defschema Model (into model/Model New))

(s/defschema ModelStripped (dissoc Model :password))

(s/defschema QueryInput (s/maybe (into model/QueryInput
                                       {(s/optional-key :username) (s/maybe String)
                                        (s/optional-key :full_name) (s/maybe String)
                                        (s/optional-key :password) (s/maybe String)})))

(s/defschema QueryResult [(s/maybe Model)])


(s/defschema LoginUser {:username String
                        :password String})

(defquery sql-user-create<! "sql/user/create.sql"
  {:connection db-spec})

(defquery sql-user-get-all "sql/user/get-all.sql"
  {:connection db-spec})

(defquery sql-user-get "sql/user/get.sql"
  {:connection db-spec})

(defquery sql-user-update<! "sql/user/update.sql"
  {:connection db-spec})

(defquery sql-get-by-username "sql/user/get-by-username.sql"
  {:connection db-spec})

(defquery sql-user-delete<! "sql/user/delete.sql"
  {:connection db-spec})


(s/defn create! [tx user :- New] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-user-create<! map-args {:connection tx})) tx user))

(s/defn get-by-username [tx username :- String]
  {:post [(s/validate Model %)]}
  (let [arguments {:username username}]
    (first
      (execute-sql-or-handle-exception
        (fn [tx map-args]
          (sql-get-by-username map-args {:connection tx})) tx arguments))))

(s/defn get-by [tx user :- QueryInput] {:post [(s/validate QueryResult %)]}
  (let [query-data (merge (initialize-query-data Model) user)]
    (execute-sql-or-handle-exception
      (fn [tx map-args]
        (sql-user-get map-args {:connection tx})) tx query-data)))

(s/defn update! [tx user :- Existing] {:post [s/validate Model %]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-user-update<! map-args {:connection tx})) tx user))

(s/defn delete! [tx user :- model/Existing] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-user-delete<! map-args {:connection tx})) tx user))