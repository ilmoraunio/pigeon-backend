(ns pigeon-backend.services.user-service
  (:require [pigeon-backend.dao.user-dao :as user-dao]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [buddy.hashers :as hashers]
            [schema.core :as s]
            [pigeon-backend.dao.user-dao :refer [NewUser LoginUser]]))

(defn user-create! [dto] {:pre [(s/validate NewUser dto)]
                          :post [(s/validate NewUser %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (let [user-with-hashed-password
            (assoc dto :password 
                       (hashers/derive (:password dto)))]
      (user-dao/create! tx user-with-hashed-password))))

(defn check-credentials [{username :username password :password :as dto}] 
                        {:pre [(s/validate LoginUser dto)]
                         :post [(instance? Boolean %)]}
    (jdbc/with-db-transaction [tx db-spec]
      (let [user-model (user-dao/get-by-username tx {:username username})]
        (if (nil? user-model)
          false
          (hashers/check password (:password user-model))))))