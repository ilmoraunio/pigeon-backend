(ns pigeon-backend.services.user-service
  (:require [pigeon-backend.dao.user-dao :as user-dao]
            [pigeon-backend.services.exception-util :refer [execute-dao-or-handle-exception]]))

(defn user-create! [dto]
  (execute-dao-or-handle-exception user-dao/create! dto))