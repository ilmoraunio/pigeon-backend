(ns pigeon-backend.dao.user-dao
  (:require [schema.core :as s]))

(s/defschema UserModel {:id s/Int 
                        :username String 
                        :full_name String 
                        :password String 
                        :deleted Boolean})

(s/defschema NewUser {:username String 
                      :full_name String 
                      :password String})

(s/defschema UserOutput {:id s/Int
                         :username String
                         :full_name String
                         :deleted Boolean})

(defn create [user] {:pre [(s/validate NewUser user)] 
                     :post [(s/validate UserOutput %)]}
  {:id 1
   :username (:username user)
   :full_name (:full_name user)
   :deleted false})

(defn read [])

(defn update [])

(defn delete [])