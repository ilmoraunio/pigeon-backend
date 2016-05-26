(ns pigeon-backend.dao.room-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao :as dao]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(def room-data {:name "Pigeon room"})

(def room-expected (contains {:id integer?}
                             {:name "Pigeon room"}
                             {:created #(instance? java.util.Date %)}
                             {:updated #(instance? java.util.Date %)}
                             {:version 0}
                             {:deleted false}))

(defn room
  ([] (let [data room-data]
        (room data)))
  ([data] (dao/create! db-spec data)))

(deftest room-dao-test
  (facts "Dao: room create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (room) => room-expected)
      (fact "Duplicate room name not allowed"
        (room) => irrelevant
        (room) => (throws clojure.lang.ExceptionInfo
                                            "Duplicate name"))))
  (facts "Dao: room get"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Get one"
        (room)
        (dao/get-by db-spec room-data) => (contains [room-expected]))
      (fact "Get all"
        (room)
        (room (assoc room-data :name "Pigeon room 2"))
        (dao/get-by db-spec nil) => (two-of coll?))
      (fact "Get some"
        (room)
        (room (assoc room-data :name "Pigeon room 2"))
        (room (assoc room-data :name "Pigeon room 3"))
        (dao/get-by db-spec {:name "Pigeon room 2"})
         => (contains [(contains {:name "Pigeon room 2"})]))))
  (facts "Dao: room update"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (let [{id :name} (room)]
          (dao/update! db-spec {:id id
                                :name "Updated pigeon room 1"})
           => (contains {:name "Updated pigeon room 1"}))))))