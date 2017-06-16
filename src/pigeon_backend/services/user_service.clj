(ns pigeon-backend.services.user-service
  (:require [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [buddy.hashers :as hashers]
            [schema.core :as s]
            [pigeon-backend.services.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]
            [pigeon-backend.dao.dao-util :refer [initialize-query-data]]
            [yesql.core :refer [defquery]]))

(s/defschema New {:username String
                  :name String
                  :password String})

(s/defschema Model (into (dissoc model/Model :id) New))

(s/defschema ModelStripped (dissoc Model :password))

(s/defschema LoginUser {:username String
                        :password String})

(defquery sql-user-create<! "sql/user/create.sql" {:connection db-spec})
(defquery sql-user-get "sql/user/get.sql" {:connection db-spec})

(s/defn user-create! [{:keys [password] :as data} :- New] {:post [(s/validate ModelStripped %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (st/select-schema
      (sql-user-create<! (assoc data :password (hashers/derive password)) {:connection tx})
      ModelStripped)))

(s/defn check-credentials [{:keys [username password]} :- LoginUser]
                          {:post [(instance? Boolean %)]}
    (jdbc/with-db-transaction [tx db-spec]
      (let [query-data (merge (initialize-query-data Model) {:username username})
            [user-model] (sql-user-get query-data {:connection tx})]
        (if (nil? user-model)
          false
          (hashers/check password (:password user-model))))))