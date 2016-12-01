(ns pigeon-backend.services.roomgroup-service
  (:require [pigeon-backend.dao.roomgroup-dao :as roomgroup-dao]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [buddy.hashers :as hashers]
            [schema.core :as s]
            [pigeon-backend.dao.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.dao.roomgroup-dao :as roomgroup-dao]))

(def AddRoomgroup {:room_id s/Int
                   :name String
                   :users_id s/Int})

(def Model roomgroup-dao/Model)

(s/defn add-roomgroup! [add-roomgroup-data :- AddRoomgroup]
  {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (roomgroup-dao/create! tx add-roomgroup-data)))