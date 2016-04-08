(ns pigeon-backend.dao.user-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]))

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

(defquery sql-create! "sql/user/create.sql"
  {:connection db-spec})

(defn create [user] {:pre [(s/validate NewUser user)] 
                     :post [(= 1 %)]}

  (sql-create! user))

(defn read [])

(defn update [])

(defn delete [])