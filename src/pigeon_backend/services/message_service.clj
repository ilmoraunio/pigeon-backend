(ns pigeon-backend.services.message-service
  (:require [pigeon-backend.dao.message :as message]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [buddy.hashers :as hashers]
            [schema.core :as s]
            [pigeon-backend.dao.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.util :as util]
            [buddy.sign.jws :as jws]
            [environ.core :refer [env]]))

(def AddMessage message/common)
(def Model message/Model)

(s/defn add-message! [input :- AddMessage]
  {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (message/create! tx input)))

(s/defn get-messages [data :- message/GetMessages]
  {:post [(s/validate message/QueryResult %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (message/get-messages tx data)))