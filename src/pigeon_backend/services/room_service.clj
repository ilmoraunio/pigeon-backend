(ns pigeon-backend.services.room-service
  (:require [pigeon-backend.dao.room-dao :as room-dao]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [buddy.hashers :as hashers]
            [schema.core :as s]
            [pigeon-backend.dao.room-dao :refer [NewRoom]]))

(defn room-create! [dto] {:pre [(s/validate NewRoom dto)]
                          ;;:post TODO add returning UserModel
                          }
  (jdbc/with-db-transaction [tx db-spec]
    (room-dao/create! tx dto)))