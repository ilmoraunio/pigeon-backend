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
            [environ.core :refer [env]]
            [pigeon-backend.services.participant-service :as participant-service]))

(def AddMessage message/common)
(def GetMessages message/GetMessages)
(def Model message/Model)
(def QueryResult message/QueryResult)

(s/defn add-message! [input :- AddMessage
                      authorization :- util/AuthorizationKey]
  {:post [(s/validate Model %)]}
  (participant-service/authorize (:room_id input) authorization)
  (jdbc/with-db-transaction [tx db-spec]
    (message/create! tx input)))

(s/defn get-messages [input :- message/GetMessages
                      authorization :- util/AuthorizationKey]
  {:post [(s/validate message/QueryResult %)]}
  (participant-service/authorize-by-participant (:room_id input)
                                                (:recipient input)
                                                authorization)
  (jdbc/with-db-transaction [tx db-spec]
    (message/get-messages tx input)))