(ns pigeon-backend.services.user-service
  (:require [pigeon-backend.dao.user-dao :as user-dao]))

(defn user-create! [dto]
  (user-dao/create! dto))