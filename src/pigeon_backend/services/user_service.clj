(ns pigeon-backend.services.user-service
  (:require [pigeon-backend.dao.user-dao :as user-dao]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [buddy.hashers :as hashers]
            [schema.core :as s]
            [pigeon-backend.dao.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.dao.user-dao :refer [New Existing ModelStripped LoginUser]]))

(def Model user-dao/Model)

(s/defn user-create! [dto :- New] {:post [(s/validate ModelStripped %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (let [user-with-hashed-password
            (assoc dto :password 
                       (hashers/derive (:password dto)))]
      (st/select-schema (user-dao/create! tx user-with-hashed-password) 
                        ModelStripped))))

(s/defn check-credentials [{username :username password :password :as dto} :- LoginUser]
                          {:post [(instance? Boolean %)]}
    (jdbc/with-db-transaction [tx db-spec]
      (let [[user-model] (user-dao/get-by tx {:username username})]
        (if (nil? user-model)
          false
          (hashers/check password (:password user-model))))))