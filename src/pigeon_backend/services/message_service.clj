(ns pigeon-backend.services.message-service
  (:require [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [schema.core :as s]
            [pigeon-backend.services.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.services.util :refer [initialize-query-data]]
            [jeesql.core :refer [defqueries]]))

(s/defschema New {:sender String
                  :recipient String
                  :message String})
(s/defschema Model (merge model/Model New {:actual_recipient String
                                           :turn s/Int}))
(s/defschema Get {:sender String
                  :recipient String})

(defqueries "sql/message.sql")

(s/defn message-create! [{:keys [recipient] :as data} :- New] {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (sql-message-create<! tx (assoc data :actual_recipient recipient))))

(s/defn message-get [data :- Get] {:post [(s/validate [(assoc Model :is_from_sender Boolean
                                                                    :turn_name String
                                                                    :sender_name String)] %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (sql-message-get tx data)))