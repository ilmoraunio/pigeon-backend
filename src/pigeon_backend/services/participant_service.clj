(ns pigeon-backend.services.participant-service
  (:require [pigeon-backend.dao.participant-dao :as participant-dao]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [buddy.hashers :as hashers]
            [schema.core :as s]
            [pigeon-backend.dao.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.dao.participant-dao :as participant-dao]))

(def AddParticipant {:room_id String
                     :name String
                     :username String})

(def Model participant-dao/Model)

(s/defn add-participant! [add-participant-data :- AddParticipant]
  {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (participant-dao/create! tx add-participant-data)))