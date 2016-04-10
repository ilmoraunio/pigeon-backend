(ns pigeon-backend.services.user-service
  (:require [pigeon-backend.dao.user-dao :as user-dao]
            [pigeon-backend.services.exception-util :refer [execute-dao-or-handle-exception]]
            [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]))

(defn user-create! [dto]
  (jdbc/with-db-transaction [tx db-spec]
    (execute-dao-or-handle-exception user-dao/create! tx dto)))