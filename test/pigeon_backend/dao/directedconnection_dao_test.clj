(ns pigeon-backend.dao.directedconnection-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao :as room-dao]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.roomgroup-dao :as roomgroup-dao]
            [pigeon-backend.dao.time-dao :as time-dao]
            [pigeon-backend.dao.directedconnection-dao :as dao]
            [pigeon-backend.dao.room-dao-test :refer [room-dto]]
            [pigeon-backend.dao.roomgroup-dao-test :refer [roomgroup-dto]]
            [pigeon-backend.dao.time-dao-test :as time-dao-test]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(defn directedconnection-dto [origin-id recipient-id]
    {:origin origin-id
     :recipient recipient-id
     :time_room_name "Pigeon room"
     :time_name "Slice of time"
     :parent nil})

(defn directedconnection-child-dto [origin-id recipient-id parent-id]
     {:origin origin-id
      :recipient recipient-id
      :time_room_name "Pigeon room"
      :time_name "Slice of time"
      :parent parent-id})

(defn directedconnection-expected [origin-id recipient-id]
      (contains {:id integer?}
                {:origin origin-id}
                {:recipient recipient-id}
                {:time_room_name "Pigeon room"}
                {:time_name "Slice of time"}
                {:parent nil}
                {:created #(instance? java.util.Date %)}
                {:updated #(instance? java.util.Date %)}
                {:version 0}
                {:deleted false}))

(defn directedconnection-child-expected [origin-id recipient-id parent-id]
      (contains {:id integer?}
                {:origin origin-id}
                {:recipient recipient-id}
                {:time_room_name "Pigeon room"}
                {:time_name "Slice of time"}
                {:parent parent-id}
                {:created #(instance? java.util.Date %)}
                {:updated #(instance? java.util.Date %)}
                {:version 0}
                {:deleted false}))

(defn directedconnection
  ([] (let [_ (room-dao-test/room)
          roomgroup-data-1 (roomgroup-dao/create! db-spec roomgroup-dto)
          roomgroup-data-2 (roomgroup-dao/create! db-spec (assoc roomgroup-dto :name "Room group 2"))
          _ (time-dao-test/time (time-dao-test/time-data :name "Slice of time"
                                                         :room_name "Pigeon room"
                                                         :sequence_order 0))
          data {:origin (:id roomgroup-data-1)
                :recipient (:id roomgroup-data-2)
                :time_room_name "Pigeon room"
                :time_name "Slice of time"
                :parent nil}]
      (dao/create! db-spec data))))

(deftest directedconnection-dao-test
  (facts "Dao: directedconnection create"
    (with-state-changes [(before :facts (empty-and-create-tables))]

      (fact "Basic case"
        (let [_ (room-dao/create! db-spec room-dto)
              roomgroup-dto-1 (roomgroup-dao/create! db-spec roomgroup-dto)
              roomgroup-dto-2 (roomgroup-dao/create! db-spec (assoc roomgroup-dto :name "Room group 2"))
              _ (time-dao-test/time (time-dao-test/time-data :name "Slice of time"
                                                             :room_name "Pigeon room"
                                                             :sequence_order 0))]
          (dao/create! db-spec (directedconnection-dto (:id roomgroup-dto-1) (:id roomgroup-dto-2)))
            => (directedconnection-expected (:id roomgroup-dto-1) (:id roomgroup-dto-2))))

      (fact "Duplicate directed connection not allowed"
        (let [_ (room-dao/create! db-spec room-dto)
              roomgroup-dto-1 (roomgroup-dao/create! db-spec roomgroup-dto)
              roomgroup-dto-2 (roomgroup-dao/create! db-spec (assoc roomgroup-dto :name "Room group 2"))
              _ (time-dao-test/time (time-dao-test/time-data :name "Slice of time"
                                                             :room_name "Pigeon room"
                                                             :sequence_order 0))
              directedconnection-dto (dao/create! db-spec (directedconnection-dto (:id roomgroup-dto-1)
                                                                                  (:id roomgroup-dto-2)))]
          (dao/create! db-spec (directedconnection-child-dto (:id roomgroup-dto-1)
                                                             (:id roomgroup-dto-2)
                                                             (:id directedconnection-dto)))
          (dao/create! db-spec (directedconnection-child-dto (:id roomgroup-dto-1)
                                                             (:id roomgroup-dto-2)
                                                             (:id directedconnection-dto)))
            => (throws clojure.lang.ExceptionInfo "Duplicate name")))

      (fact "Tree structure with parent"
        (let [_ (room-dao/create! db-spec room-dto)
              roomgroup-dto-1 (roomgroup-dao/create! db-spec roomgroup-dto)
              roomgroup-dto-2 (roomgroup-dao/create! db-spec (assoc roomgroup-dto :name "Room group 2"))
              roomgroup-dto-3 (roomgroup-dao/create! db-spec (assoc roomgroup-dto :name "Room group 3"))
              _ (time-dao-test/time (time-dao-test/time-data :name "Slice of time"
                                                             :room_name "Pigeon room"
                                                             :sequence_order 0))
              directedconnection-dto (dao/create! db-spec (directedconnection-dto (:id roomgroup-dto-1)
                                                                                  (:id roomgroup-dto-2)))]
              
              (dao/create! db-spec (directedconnection-child-dto (:id roomgroup-dto-2)
                                                                 (:id roomgroup-dto-3)
                                                                 (:id directedconnection-dto)))
                => (directedconnection-child-expected (:id roomgroup-dto-2)
                                                      (:id roomgroup-dto-3)
                                                      (:id directedconnection-dto)))))))