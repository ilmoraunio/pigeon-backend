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
  ([{:keys [sender
            intended_recipient
            actual_recipient
            room_id
            time_id]}]
   {:sender_roomgroup_id sender
    :intended_recipient_roomgroup_id intended_recipient
    :actual_recipient_roomgroup_id actual_recipient
    :body "Foobar"
    :room_id room_id
    :time_id time_id}))

(defn message-data-expected
  ([{:keys [sender intended_recipient actual_recipient
              room_id time_id]}]
    (contains {:id integer?}
              {:sender_roomgroup_id sender}
              {:intended_recipient_roomgroup_id intended_recipient}
              {:actual_recipient_roomgroup_id actual_recipient}
              {:body "Foobar"}
              {:room_id room_id}
              {:time_id time_id}
              {:created #(instance? java.util.Date %)}
              {:updated #(instance? java.util.Date %)}
              {:version 0}
              {:deleted false})))

(defn message
  ([data] (dao/create! db-spec (message-data data))))

(deftest message-dao-test
  (facts "Dao: message create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      
      (fact "Basic case"
        (let [{room_id :id} (room-dao-test/room)
              {roomgroup-1-id :id} (roomgroup-dao-test/roomgroup {:room_id room_id})
              {roomgroup-2-id :id} (roomgroup-dao-test/roomgroup {:name "Room group 2" :room_id room_id})
              {time_id :id} (time-dao-test/time (time-dao-test/time-data {:name "Slice of time"
                                                                          :room_id room_id
                                                                          :sequence_order 0}))]
          (message {:sender roomgroup-1-id
                    :intended_recipient roomgroup-2-id
                    :actual_recipient roomgroup-2-id
                    :room_id room_id
                    :time_id time_id}) => (message-data-expected {:sender roomgroup-1-id
                                                                  :intended_recipient roomgroup-2-id
                                                                  :actual_recipient roomgroup-2-id
                                                                  :room_id room_id
                                                                  :time_id time_id})))

      (fact "Multiple messages OK"
        (let [{room_id :id} (room-dao-test/room)
              {roomgroup-1-id :id} (roomgroup-dao-test/roomgroup {:room_id room_id})
              {roomgroup-2-id :id} (roomgroup-dao-test/roomgroup {:name "Room group 2" :room_id room_id})
              {time_id :id} (time-dao-test/time (time-dao-test/time-data {:name "Slice of time"
                                                                          :room_id room_id
                                                                          :sequence_order 0}))]
          (message {:sender roomgroup-1-id
                    :intended_recipient roomgroup-2-id
                    :actual_recipient roomgroup-2-id
                    :room_id room_id
                    :time_id time_id}) => irrelevant

          (message {:sender roomgroup-1-id
                    :intended_recipient roomgroup-2-id
                    :actual_recipient roomgroup-2-id
                    :room_id room_id
                    :time_id time_id}) => (message-data-expected {:sender roomgroup-1-id
                                                                  :intended_recipient roomgroup-2-id
                                                                  :actual_recipient roomgroup-2-id
                                                                  :room_id room_id
                                                                  :time_id time_id}))))))