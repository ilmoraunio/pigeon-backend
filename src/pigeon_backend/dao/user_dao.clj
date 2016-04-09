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

(defn invoke-sql [f dto db-spec]
  (try
    (f dto db-spec)
    (catch BatchUpdateException e
      (let [message (-> e .getNextException .getMessage)
            duplicate-user #"username.*?already exists"]
        (when-let [findings (re-find duplicate-user message)]
          (throw
            (ex-info
              (format "User %s already exists" (:username dto))
              dto)))
        (throw (.getNextException e))))))

(defn create! [user] {:pre [(s/validate NewUser user)]
                     :post [(true? %)]}
  (invoke-sql 
    (fn [dto db-spec]
      (jdbc/with-db-transaction [tx db-spec]
        (sql-user-create! dto {:connection tx}))
      true) 
    user
    db-spec))

(defn get-from-db [])

(defn update! [])

(defn delete! [])