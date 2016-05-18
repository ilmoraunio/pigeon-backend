(ns pigeon-backend.dao.directedconnection-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao :as room-dao]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.roomgroup-dao :as roomgroup-dao]
            [pigeon-backend.dao.time-dao :as time-dao]
            [pigeon-backend.dao.directedconnection-dao :as dao]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.roomgroup-dao-test :as roomgroup-dao-test]
            [pigeon-backend.dao.time-dao-test :as time-dao-test]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(defn directedconnection-data
  ([& {:keys [origin recipient parent]}]
   {:origin origin
    :recipient recipient
    :time_room_name "Pigeon room"
    :time_name "Slice of time"
    :parent parent}))

(defn directedconnection-expected
  ([& {:keys [origin recipient parent]}]
      (contains {:id integer?}
                {:origin origin}
                {:recipient recipient}
                {:time_room_name "Pigeon room"}
                {:time_name "Slice of time"}
                {:parent parent}
                {:created #(instance? java.util.Date %)}
                {:updated #(instance? java.util.Date %)}
                {:version 0}
                {:deleted false})))

(defn directedconnection
  ([] (let [_ (room-dao-test/room)
            roomgroup-data-1 (roomgroup-dao-test/roomgroup)
            roomgroup-data-2 (roomgroup-dao-test/roomgroup (roomgroup-dao-test/roomgroup-data :name "Room group 2"))
            _ (time-dao-test/time (time-dao-test/time-data :name "Slice of time"
                                                           :room_name "Pigeon room"
                                                           :sequence_order 0))
            data (directedconnection-data :origin (:id roomgroup-data-1)
                                          :recipient (:id roomgroup-data-2))]
      (directedconnection data)))
  ([data] (dao/create! db-spec data)))

(deftest directedconnection-dao-test
  (facts "Dao: directedconnection create"
    (with-state-changes [(before :facts (empty-and-create-tables))]

      (fact "Basic case"
        (let [_ (room-dao-test/room)
              roomgroup-dto-1 (roomgroup-dao-test/roomgroup)
              roomgroup-dto-2 (roomgroup-dao-test/roomgroup (roomgroup-dao-test/roomgroup-data :name "Room group 2"))
              _ (time-dao-test/time (time-dao-test/time-data :name "Slice of time"
                                                             :room_name "Pigeon room"
                                                             :sequence_order 0))]
          (directedconnection (directedconnection-data :origin (:id roomgroup-dto-1)
                                                       :recipient (:id roomgroup-dto-2)))
            => (directedconnection-expected :origin (:id roomgroup-dto-1)
                                            :recipient (:id roomgroup-dto-2))))

      (fact "Duplicate directed connection not allowed"
        (let [_ (room-dao-test/room)
              roomgroup-dto-1 (roomgroup-dao-test/roomgroup)
              roomgroup-dto-2 (roomgroup-dao-test/roomgroup (roomgroup-dao-test/roomgroup-data :name "Room group 2"))
              _ (time-dao-test/time (time-dao-test/time-data :name "Slice of time"
                                                             :room_name "Pigeon room"
                                                             :sequence_order 0))
              directedconnection-dto (directedconnection (directedconnection-data :origin (:id roomgroup-dto-1)
                                                                                  :recipient (:id roomgroup-dto-2)))]
          (directedconnection (directedconnection-data :origin (:id roomgroup-dto-1)
                                                       :recipient (:id roomgroup-dto-2)
                                                       :parent (:id directedconnection-dto)))
          (directedconnection (directedconnection-data :origin (:id roomgroup-dto-1)
                                                       :recipient (:id roomgroup-dto-2)
                                                       :parent (:id directedconnection-dto)))
            => (throws clojure.lang.ExceptionInfo "Duplicate name")))

      (fact "Tree structure with parent"
        (let [_ (room-dao-test/room)
              roomgroup-dto-1 (roomgroup-dao-test/roomgroup)
              roomgroup-dto-2 (roomgroup-dao-test/roomgroup (roomgroup-dao-test/roomgroup-data :name "Room group 2"))
              roomgroup-dto-3 (roomgroup-dao-test/roomgroup (roomgroup-dao-test/roomgroup-data :name "Room group 3"))
              _ (time-dao-test/time (time-dao-test/time-data :name "Slice of time"
                                                             :room_name "Pigeon room"
                                                             :sequence_order 0))
              directedconnection-dto (directedconnection (directedconnection-data :origin (:id roomgroup-dto-1)
                                                                                  :recipient (:id roomgroup-dto-2)))]
              
              (directedconnection (directedconnection-data :origin    (:id roomgroup-dto-2)
                                                           :recipient (:id roomgroup-dto-3)
                                                           :parent    (:id directedconnection-dto)))
                => (directedconnection-expected :origin (:id roomgroup-dto-2)
                                                :recipient (:id roomgroup-dto-3)
                                                :parent (:id directedconnection-dto)))))))