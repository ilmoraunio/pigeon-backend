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
  ([{:keys [origin recipient parent time_id]}]
   {:origin origin
    :recipient recipient
    :time_id time_id
    :parent parent}))

(defn directedconnection-expected
  ([{:keys [origin recipient parent]}]
      (contains {:id integer?}
                {:origin origin}
                {:recipient recipient}
                {:time_id integer?}
                {:parent parent}
                {:created #(instance? java.util.Date %)}
                {:updated #(instance? java.util.Date %)}
                {:version 0}
                {:deleted false})))

(defn directedconnection
  ([] (let [{room_id :id} (room-dao-test/room)
            roomgroup-data-1 (roomgroup-dao-test/roomgroup {:room_id room_id})
            roomgroup-data-2 (roomgroup-dao-test/roomgroup {:name "Room group 2"
                                                            :room_id room_id})
            {time_id :id} (time-dao-test/time (time-dao-test/time-data {:name "Slice of time"
                                                                        :room_id room_id
                                                                        :sequence_order 0}))]
      (directedconnection {:origin (:id roomgroup-data-1)
                           :recipient (:id roomgroup-data-2)
                           :time_id time_id})))
  ([data] (dao/create! db-spec (directedconnection-data data))))

(deftest directedconnection-dao-test
  (facts "Dao: directedconnection create"
    (with-state-changes [(before :facts (empty-and-create-tables))]

      (fact "Basic case"
        (let [{room_id :id} (room-dao-test/room)
              roomgroup-dto-1 (roomgroup-dao-test/roomgroup {:room_id room_id})
              roomgroup-dto-2 (roomgroup-dao-test/roomgroup {:name "Room group 2"
                                                             :room_id room_id})
              {time_id :id} (time-dao-test/time (time-dao-test/time-data {:name "Slice of time"
                                                                          :room_id room_id
                                                                          :sequence_order 0}))]
          (directedconnection {:origin (:id roomgroup-dto-1)
                               :recipient (:id roomgroup-dto-2)
                               :time_id time_id})
            => (directedconnection-expected {:origin (:id roomgroup-dto-1)
                                             :recipient (:id roomgroup-dto-2)})))

      (fact "Duplicate directed connection not allowed"
        (let [{room_id :id} (room-dao-test/room)
              roomgroup-dto-1 (roomgroup-dao-test/roomgroup {:room_id room_id})
              roomgroup-dto-2 (roomgroup-dao-test/roomgroup {:name "Room group 2"
                                                             :room_id room_id})
              {time_id :id} (time-dao-test/time (time-dao-test/time-data {:name "Slice of time"
                                                                          :room_id room_id
                                                                          :sequence_order 0}))
              directedconnection-dto (directedconnection {:origin (:id roomgroup-dto-1)
                                                          :recipient (:id roomgroup-dto-2)
                                                          :time_id time_id})]
          (directedconnection {:origin (:id roomgroup-dto-1)
                               :recipient (:id roomgroup-dto-2)
                               :parent (:id directedconnection-dto)
                               :time_id time_id})
          (directedconnection {:origin (:id roomgroup-dto-1)
                               :recipient (:id roomgroup-dto-2)
                               :parent (:id directedconnection-dto)
                               :time_id time_id})
            => (throws clojure.lang.ExceptionInfo "Duplicate connection")))

      (fact "Tree structure with parent"
        (let [{room_id :id} (room-dao-test/room)
              roomgroup-dto-1 (roomgroup-dao-test/roomgroup {:room_id room_id})
              roomgroup-dto-2 (roomgroup-dao-test/roomgroup {:name "Room group 2"
                                                             :room_id room_id})
              roomgroup-dto-3 (roomgroup-dao-test/roomgroup {:name "Room group 3"
                                                             :room_id room_id})
              {time_id :id} (time-dao-test/time (time-dao-test/time-data {:name "Slice of time"
                                                                          :room_id room_id
                                                                          :sequence_order 0}))
              directedconnection-dto (directedconnection {:origin (:id roomgroup-dto-1)
                                                          :recipient (:id roomgroup-dto-2)
                                                          :time_id time_id})]
              
              (directedconnection {:origin    (:id roomgroup-dto-2)
                                   :recipient (:id roomgroup-dto-3)
                                   :parent    (:id directedconnection-dto)
                                   :time_id time_id})
                => (directedconnection-expected {:origin (:id roomgroup-dto-2)
                                                 :recipient (:id roomgroup-dto-3)
                                                 :parent (:id directedconnection-dto)}))))))  