(ns pigeon-backend.dao.time-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.time-dao :as dao]
            [pigeon-backend.dao.room-dao :as room-dao]
            [pigeon-backend.dao.room-dao-test :refer [room-dto]]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(defn time-dto [time-name room-name sequence-order]
  {:room_name room-name
   :name time-name
   :sequence_order sequence-order})

(defn time-expected [time-name room-name sequence-order]
  (contains {:name time-name}
            {:room_name room-name}
            {:sequence_order sequence-order}
            {:created #(instance? java.util.Date %)}
            {:updated #(instance? java.util.Date %)}
            {:version 0}
            {:deleted false}))

(deftest time-dao-test
  (facts "Dao: time create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (let [{room-name :name} (room-dao/create! db-spec room-dto)
              {time-name :name 
               room-name :room_name 
               sequence-order :sequence_order :as returned-dto}
                (dao/create! db-spec (time-dto "Slice of time" room-name 0))]
            returned-dto => (time-expected time-name room-name sequence-order)))
      (fact "Multiple"
        (let [{room-name :name} (room-dao/create! db-spec room-dto)
              _ (dao/create! db-spec (time-dto "Slice of time" room-name 0))
              {time-name :name 
               room-name :room_name 
               sequence-order :sequence_order :as returned-dto}
                (dao/create! db-spec (time-dto "Another slice of time" room-name 1))]
            returned-dto => (time-expected time-name room-name sequence-order)))
      (fact "Duplicate time inside room not allowed"
        (let [{room-name :name} (room-dao/create! db-spec room-dto)]
          (dao/create! db-spec (time-dto "Slice of time" room-name 0))
          (dao/create! db-spec (time-dto "Slice of time" room-name 1))
            => (throws clojure.lang.ExceptionInfo
                "Duplicate name"))))))
