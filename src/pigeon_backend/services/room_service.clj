(ns pigeon-backend.services.room-service
  (:require [pigeon-backend.dao.room-dao :as room-dao]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [buddy.hashers :as hashers]
            [schema.core :as s]
            [pigeon-backend.dao.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.dao.room-dao :refer [New Model Existing ServiceQueryInput QueryResult]]))

(s/defn room-create! [data :- New] {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (room-dao/create! tx data)))

(s/defn room-update! [room :- Existing]
  {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (room-dao/update! tx room)))

(s/defn room-get-by [room :- ServiceQueryInput]
  {:post [(s/validate QueryResult %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (room-dao/get-by tx room)))

(s/defn room-delete! [room :- model/Existing]
  {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (room-dao/delete! tx room)))