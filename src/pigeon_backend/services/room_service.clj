(ns pigeon-backend.services.room-service
  (:require [pigeon-backend.dao.room-dao :as room-dao]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [buddy.hashers :as hashers]
            [schema.core :as s]
            [pigeon-backend.dao.room-dao :refer [Input Model]]))

(s/defn room-create! [data :- Input] {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (room-dao/create! tx data)))