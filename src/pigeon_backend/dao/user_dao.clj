(ns pigeon-backend.dao.user-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [clojure.java.jdbc :as jdbc])
  (import org.postgresql.util.PSQLException))

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

(defn user-create [user] {:pre [(s/validate NewUser user)]
                     :post [(instance? Boolean %)]}
  (try 
    (jdbc/with-db-transaction [tx db-spec]
      (sql-user-create! user {:connection tx})
      true)
    (catch Exception e
      (binding [*out* *err*]
        (println (.getNextException e)))
      false)))

(defn user-read [])

(defn user-update [])

(defn user-delete [])