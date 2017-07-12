(ns pigeon-backend.services.turn-service
  (:require [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [schema.core :as s]
            [pigeon-backend.services.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.services.util :refer [initialize-query-data]]
            [jeesql.core :refer [defqueries]]))

(s/defschema Model (merge model/Model {:name String
                                       :ordering s/Int
                                       :active Boolean}))

(defqueries "sql/turn.sql")

(s/defn turn-get []
  {:post [(s/validate [Model] %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (sql-turn-get tx)))