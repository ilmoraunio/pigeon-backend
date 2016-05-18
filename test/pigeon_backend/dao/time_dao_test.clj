(ns pigeon-backend.dao.time-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.time-dao :as dao]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(defn time-data
  ([& {:keys [room_name name sequence_order]}]
    {:room_name room_name
     :name name
     :sequence_order sequence_order}))

(defn time-expected 
  ([& {:keys [time-name room-name sequence-order]}]
    (contains {:name time-name}
              {:room_name room-name}
              {:sequence_order sequence-order}
              {:created #(instance? java.util.Date %)}
              {:updated #(instance? java.util.Date %)}
              {:version 0}
              {:deleted false})))

(defn time
  ([data] (dao/create! db-spec data)))

(deftest time-dao-test
  (facts "Dao: time create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (let [{room-name :name} (room-dao-test/room)
              {time-name :name 
               room-name :room_name 
               sequence-order :sequence_order :as returned-data}
                (time (time-data :name "Slice of time" 
                                 :room_name room-name 
                                 :sequence_order 0))]
            returned-data => (time-expected :time-name time-name 
                                           :room-name room-name 
                                           :sequence-order sequence-order)))
      (fact "Multiple"
        (let [{room-name :name} (room-dao-test/room)
              _ (time (time-data :name "Slice of time" 
                                 :room_name room-name 
                                 :sequence_order 0))
              {time-name :name 
               room-name :room_name 
               sequence-order :sequence_order :as returned-data}
                (time (time-data :name "Another slice of time" 
                                 :room_name room-name 
                                 :sequence_order 1))]
            returned-data => (time-expected :time-name time-name 
                                           :room-name room-name 
                                           :sequence-order sequence-order)))
      (fact "Duplicate time inside room not allowed"
        (let [{room-name :name} (room-dao-test/room)]
          (time (time-data :name "Slice of time" 
                                 :room_name room-name 
                                 :sequence_order 0))
          (time (time-data :name "Slice of time" 
                                 :room_name room-name 
                                 :sequence_order 1))
            => (throws clojure.lang.ExceptionInfo
                "Duplicate name"))))))
