(ns pigeon-backend.dao.message-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao :as room-dao]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.roomgroup-dao :as roomgroup-dao]
            [pigeon-backend.dao.time-dao :as time-dao]
            [pigeon-backend.dao.message-dao :as dao]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.roomgroup-dao-test :as roomgroup-dao-test]
            [pigeon-backend.dao.time-dao-test :as time-dao-test]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(defn message-data
  ([& {:keys [sender intended_recipient actual_recipient
              room_name time_room_name time_name]}]
   {:sender_roomgroup_id sender
    :intended_recipient_roomgroup_id intended_recipient
    :actual_recipient_roomgroup_id actual_recipient
    :body "Foobar"
    :room_name room_name
    :time_room_name time_room_name
    :time_name time_name}))

(defn message-data-expected
  ([& {:keys [sender intended_recipient actual_recipient
              room_name time_room_name time_name]}]
    (contains {:id integer?}
              {:sender_roomgroup_id sender}
              {:intended_recipient_roomgroup_id intended_recipient}
              {:actual_recipient_roomgroup_id actual_recipient}
              {:body "Foobar"}
              {:room_name room_name}
              {:time_room_name time_room_name}
              {:time_name time_name}
              {:created #(instance? java.util.Date %)}
              {:updated #(instance? java.util.Date %)}
              {:version 0}
              {:deleted false})))

(defn message
  ([data] (dao/create! db-spec data)))

(deftest message-dao-test
  (facts "Dao: message create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      
      (fact "Basic case"
        (let [{room_name :name} (room-dao-test/room)
              roomgroup-1 (roomgroup-dao-test/roomgroup)
              roomgroup-2 (roomgroup-dao-test/roomgroup (roomgroup-dao-test/roomgroup-data :name "Room group 2"))
              time (time-dao-test/time (time-dao-test/time-data :name "Slice of time"
                                                                :room_name room_name
                                                                :sequence_order 0))
              data (message-data :sender (:id roomgroup-1)
                                 :intended_recipient (:id roomgroup-2)
                                 :actual_recipient (:id roomgroup-2)
                                 :room_name room_name
                                 :time_room_name room_name
                                 :time_name "Slice of time")]
          (message data) => (message-data-expected :sender (:id roomgroup-1)
                                                   :intended_recipient (:id roomgroup-2)
                                                   :actual_recipient (:id roomgroup-2)
                                                   :room_name room_name
                                                   :time_room_name room_name
                                                   :time_name "Slice of time")))

      (fact "Multiple messages OK"
        (let [{room_name :name} (room-dao-test/room)
              roomgroup-1 (roomgroup-dao-test/roomgroup)
              roomgroup-2 (roomgroup-dao-test/roomgroup (roomgroup-dao-test/roomgroup-data :name "Room group 2"))
              time (time-dao-test/time (time-dao-test/time-data :name "Slice of time"
                                                                :room_name room_name
                                                                :sequence_order 0))
              data (message-data :sender (:id roomgroup-1)
                                 :intended_recipient (:id roomgroup-2)
                                 :actual_recipient (:id roomgroup-2)
                                 :room_name room_name
                                 :time_room_name room_name
                                 :time_name "Slice of time")]
          (message data) => irrelevant
          (message data) => (message-data-expected :sender (:id roomgroup-1)
                                                   :intended_recipient (:id roomgroup-2)
                                                   :actual_recipient (:id roomgroup-2)
                                                   :room_name room_name
                                                   :time_room_name room_name
                                                   :time_name "Slice of time"))))))