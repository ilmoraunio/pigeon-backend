(ns pigeon-backend.services.participant-service
  (:require [pigeon-backend.dao.participant-dao :as participant-dao]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [buddy.hashers :as hashers]
            [schema.core :as s]
            [pigeon-backend.dao.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.dao.participant-dao :as participant-dao :refer [QueryResult]]
            [pigeon-backend.util :as util]
            [buddy.sign.jws :as jws]
            [environ.core :refer [env]]))

(def AddParticipant {:room_id String
                     :name String
                     :username String})

(defn authorize-common [authorized?]
  (if (not authorized?)
    (throw
      (ex-info
        "Authorization not granted"
        {:type :authorization
         :cause "User is not a room participant"}))))

(s/defn authorize [room-id :- String,
                   authorization :- util/AuthorizationKey]
  (let [username (:user (jws/unsign authorization (env :jws-shared-secret)))
        authorized? (jdbc/with-db-transaction [tx db-spec]
                      (participant-dao/get-auth tx {:room_id room-id
                                                    :username username}))]
    (authorize-common authorized?)))

(s/defn authorize-by-participant [room-id :- String,
                                  sender-id :- String,
                                  participant-id :- String,
                                  authorization :- util/AuthorizationKey]
  (let [username (:user (jws/unsign authorization (env :jws-shared-secret)))
        authorized? (jdbc/with-db-transaction [tx db-spec]
                      (participant-dao/get-auth-by-participant tx {:room_id room-id
                                                                   :sender sender-id
                                                                   :recipient participant-id
                                                                   :username username}))]
    (authorize-common authorized?)))

(def Model participant-dao/Model)

(s/defn add-participant! [add-participant-data :- AddParticipant]
  {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (participant-dao/create! tx add-participant-data)))

(s/defn get-by-room [room-id :- String,
                     authorization :- util/AuthorizationKey]
  {:post [(s/validate QueryResult %)]}
  (authorize room-id authorization)
  (jdbc/with-db-transaction [tx db-spec]
    (participant-dao/get-by tx {:room_id room-id})))