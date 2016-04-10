(ns pigeon-backend.dao.user-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exceptions]]
            [clojure.java.jdbc :as jdbc]))

(s/defschema UserModel {:id s/Int 
                        :username String 
                        :full_name String 
                        :password String 
                        :deleted Boolean})

(s/defschema NewUser {:username String 
                      :full_name String 
                      :password String})

;; TODO: NewUserr password transformation

(s/defschema PersistedUser {:id s/Int
                            :username String
                            :full_name String
                            :deleted Boolean})

(defquery sql-user-create! "sql/user/create.sql"
  {:connection db-spec})

(defn create! [user] {:pre [(s/validate NewUser user)]
                     :post [(true? %)]}
  (execute-sql-or-handle-exceptions
    (fn [db-spec map-args]
      (jdbc/with-db-transaction [tx db-spec]
        (sql-user-create! map-args {:connection tx}))
      true)
    db-spec
    user))

(defn get-from-db [])

(defn update! [])

(defn delete! [])