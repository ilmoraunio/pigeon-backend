(ns pigeon-backend.dao.user-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]))

(s/defschema UserModel {:id s/Int 
                        :username String 
                        :full_name String 
                        :password String 
                        :deleted Boolean})

(s/defschema NewUser {:username String 
                      :full_name String 
                      :password String})

;; TODO: NewUser password transformation

(s/defschema PersistedUser {:id s/Int
                            :username String
                            :full_name String
                            :deleted Boolean})

(defquery sql-user-create! "sql/user/create.sql"
  {:connection db-spec})

(defquery sql-user-get-all "sql/user/get-all.sql"
  {:connection db-spec})

(defn create! [tx user] {:pre [(s/validate NewUser user)]
                         :post [(s/validate NewUser %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-user-create! map-args {:connection tx})
      user) tx user))

(defn get-from-db [])

(defn update! [])

(defn delete! [])