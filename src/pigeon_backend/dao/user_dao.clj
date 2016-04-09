(ns pigeon-backend.dao.user-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [clojure.java.jdbc :as jdbc])
  (import org.postgresql.util.PSQLException)
  (import java.sql.BatchUpdateException))

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
  (try
    (jdbc/with-db-transaction [tx db-spec]
      (sql-user-create! user {:connection tx})
      true)
    (catch BatchUpdateException e
      (let [message (-> e .getNextException .getMessage)
            duplicate-user #"duplicate key value violates unique constraint"]
        (when-let [[_ field] (re-find duplicate-user message)]
          (throw
            (ex-info
              (format "Duplicate user %s found" (:username user))
              user)))))))

(defn get-from-db [])

(defn update! [])

(defn delete! [])