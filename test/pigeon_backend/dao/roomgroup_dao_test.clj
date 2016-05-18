(ns pigeon-backend.dao.roomgroup-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.roomgroup-dao :as dao]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(defn roomgroup-data
  ([& {:keys [name parent]}]
    {:room_name "Pigeon room"
     :name name
     :parent parent}))

(def roomgroup-expected (contains {:id integer?}
                                  {:room_name "Pigeon room"}
                                  {:name "Room group"}
                                  {:parent nil}
                                  {:created #(instance? java.util.Date %)}
                                  {:updated #(instance? java.util.Date %)}
                                  {:version 0}
                                  {:deleted false}))

(defn roomgroup-child-expected [id]
                        (contains {:id integer?}
                                  {:room_name "Pigeon room"}
                                  {:name "Room group child"}
                                  {:parent id}
                                  {:created #(instance? java.util.Date %)}
                                  {:updated #(instance? java.util.Date %)}
                                  {:version 0}
                                  {:deleted false}))

(defn roomgroup
  ([] (let [data (roomgroup-data :name "Room group")]
        (roomgroup data)))
  ([data] (dao/create! db-spec data)))

(deftest roomgroup-dao-test
  (facts "Dao: roomgroup create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (room-dao-test/room)
        (roomgroup) => roomgroup-expected)
      (fact "Duplicate group name inside room not allowed"
        (room-dao-test/room)
        (roomgroup)
        (roomgroup)
          => (throws clojure.lang.ExceptionInfo
              "Duplicate name"))
      (fact "Tree structure with parent"
        (room-dao-test/room)
        (let [{:keys [id]} (roomgroup)
              roomgroup-child-data (roomgroup-data :name "Room group child"
                                                   :parent id)]
          (roomgroup roomgroup-child-data)
            => (roomgroup-child-expected id))))))