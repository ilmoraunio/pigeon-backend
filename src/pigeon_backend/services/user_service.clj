(ns pigeon-backend.services.user-service
  (:require [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [buddy.hashers :as hashers]
            [schema.core :as s]
            [pigeon-backend.services.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.services.util :refer [initialize-query-data]]
            [jeesql.core :refer [defqueries]]))

(s/defschema New {:username String
                  :name String
                  :password String})
(s/defschema Model (into (dissoc model/Model :id) New))
(s/defschema ModelStripped (dissoc Model :password))
(s/defschema LoginUser {:username String
                        :password String})

(defqueries "sql/user.sql")
(defqueries "sql/visibility.sql")

(s/defn user-create! [{:keys [password] :as data} :- New] {:post [(s/validate ModelStripped %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (st/select-schema
      (sql-user-create<! tx (assoc data :password (hashers/derive password)))
      ModelStripped)))

(s/defn list-users [{:keys [username]} :- {:username String}]
  (let [visible-users-for-user (->> (filter #(= (:from_node %) username)
                                            (sql-get-visibilities db-spec))
                                    (map :to_nodes)
                                    flatten
                                    set)]
    (filter #(contains? visible-users-for-user (:username %))
            (sql-list-users db-spec))))

(s/defn check-credentials [{:keys [username password]} :- LoginUser]
                          {:post [(instance? Boolean %)]}
    (jdbc/with-db-transaction [tx db-spec]
      (let [query-data (merge (initialize-query-data Model) {:username username})
            [user-model] (sql-user-get tx query-data)]
        (if (nil? user-model)
          false
          (hashers/check password (:password user-model))))))

(s/defn is-moderator? [data :- {:username String}]
  {:post [(instance? Boolean %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (->> (sql-is-moderator? tx data)
         first
         :is_moderator)))