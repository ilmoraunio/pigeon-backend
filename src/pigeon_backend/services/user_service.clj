(ns pigeon-backend.services.user-service
  (:require [pigeon-backend.dao.user-dao :as user-dao]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]))

(defn user-create! [dto]
  (jdbc/with-db-transaction [tx db-spec]
    (user-dao/create! tx dto)))