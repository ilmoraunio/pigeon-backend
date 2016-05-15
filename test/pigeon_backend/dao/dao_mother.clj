(ns pigeon-backend.dao.dao-mother
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao :as room-dao]
            [pigeon-backend.dao.roomgroup-dao :as roomgroup-dao]
            [pigeon-backend.dao.time-dao :as time-dao]
            [pigeon-backend.dao.directedconnection-dao :as dao]
            [pigeon-backend.dao.room-dao-test :refer [room-dto]]
            [pigeon-backend.dao.roomgroup-dao-test :refer [roomgroup-dto]]
            [pigeon-backend.dao.time-dao-test :refer [time-dto]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(defn directedconnection []
  (let [_ (room-dao/create! db-spec room-dto)
        roomgroup-data-1 (roomgroup-dao/create! db-spec roomgroup-dto)
        roomgroup-data-2 (roomgroup-dao/create! db-spec (assoc roomgroup-dto :name "Room group 2"))
        _ (time-dao/create! db-spec (time-dto "Slice of time" "Pigeon room" 0))
        data {:origin (:id roomgroup-data-1)
              :recipient (:id roomgroup-data-2)
              :time_room_name "Pigeon room"
              :time_name "Slice of time"
              :parent nil}]
    (dao/create! db-spec data)))