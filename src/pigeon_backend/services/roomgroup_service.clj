(ns pigeon-backend.services.roomgroup-service
  (:require [pigeon-backend.dao.roomgroup-dao :as roomgroup-dao]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [buddy.hashers :as hashers]
            [schema.core :as s]
            [pigeon-backend.dao.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.dao.roomgroup-dao :refer [New Model Existing ServiceQueryInput QueryResult]]))

(s/defn roomgroup-create! [data :- New] {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (roomgroup-dao/create! tx data)))

(s/defn roomgroup-update! [roomgroup :- Existing]
  {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (roomgroup-dao/update! tx roomgroup)))

(s/defn roomgroup-get-by [roomgroup :- ServiceQueryInput]
  {:post [(s/validate QueryResult %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (roomgroup-dao/get-by tx roomgroup)))

(s/defn roomgroup-delete! [roomgroup :- model/Existing]
  {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (roomgroup-dao/delete! tx roomgroup)))