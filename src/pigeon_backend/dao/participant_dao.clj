(ns pigeon-backend.dao.participant-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]
            [pigeon-backend.dao.dao-util :refer [initialize-query-data]]
            [pigeon-backend.dao.model :as model]))

(def common {:room_id String
             :name String
             :username (s/maybe String)})

(def New common)

(def Existing (into model/Existing common))

(def Model (into model/Model common))

(def ^{:private true} QueryInput {(s/optional-key :room_id) (s/maybe String)
                                  (s/optional-key :name) (s/maybe String)
                                  (s/optional-key :username) (s/maybe String)})

(def QueryInput (s/maybe (into model/QueryInput
                               QueryInput)))

(def QueryResult [(s/maybe Model)])

(def AuthInput {:room_id String
                :username String})
(def AuthInputByParticipant (assoc AuthInput :participant String))

(defquery sql-participant-create<! "sql/participant/create.sql"
  {:connection db-spec})

(defquery sql-participant-get "sql/participant/get.sql"
  {:connection db-spec})

(defquery sql-get-auth "sql/participant/get-auth.sql"
  {:connection db-spec})

(defquery sql-get-auth-by-participant "sql/participant/get-auth-by-participant.sql"
  {:connection db-spec})

(s/defn create! [tx participant :- New] {:post [(s/validate Model %)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-participant-create<! map-args {:connection tx})) tx participant))

(s/defn get-by [tx participant :- QueryInput] {:post [(s/validate QueryResult %)]}
  (let [query-data (merge (initialize-query-data Model) participant)]
    (execute-sql-or-handle-exception
      (fn [tx map-args]
        (sql-participant-get map-args {:connection tx})) tx query-data)))

(s/defn get-auth [tx auth :- AuthInput] {:post [(instance? Boolean %)]}
  (let [[{is_authorized? :is_authorized}]
          (execute-sql-or-handle-exception
            (fn [tx map-args]
              (sql-get-auth map-args {:connection tx})) tx auth)]
    is_authorized?))

(s/defn get-auth-by-participant [tx auth :- AuthInputByParticipant] {:post [(instance? Boolean %)]}
  (let [[{is_authorized? :is_authorized}]
        (execute-sql-or-handle-exception
          (fn [tx map-args]
            (sql-get-auth-by-participant map-args {:connection tx})) tx auth)]
    is_authorized?))