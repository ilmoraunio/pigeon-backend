(ns pigeon-backend.dao.roomgroup-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.roomgroup-dao :as dao]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(defn roomgroup-data
  ([{:keys [room_id name parent]
     :or {name "Room group" parent nil}}]
   {:room_id room_id
    :name name
    :parent parent
    :users_id nil}))

(def roomgroup-expected (contains {:id integer?}
                                  {:room_id integer?}
                                  {:name "Room group"}
                                  {:parent nil}
                                  {:users_id nil}
                                  {:created #(instance? java.util.Date %)}
                                  {:updated #(instance? java.util.Date %)}
                                  {:version 0}
                                  {:deleted false}))

(defn roomgroup-child-expected [id]
                        (contains {:id integer?}
                                  {:room_id integer?}
                                  {:name "Room group child"}
                                  {:parent id}
                                  {:users_id nil}
                                  {:created #(instance? java.util.Date %)}
                                  {:updated #(instance? java.util.Date %)}
                                  {:version 0}
                                  {:deleted false}))

(defn roomgroup
  ([input] (dao/create! db-spec (roomgroup-data input))))

(deftest roomgroup-dao-test
  (facts "Dao: roomgroup create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (let [{room_id :id} (room-dao-test/room)]
          (roomgroup {:room_id room_id}) => roomgroup-expected))
      (fact "Duplicate group name inside room not allowed"
        (let [{room_id :id} (room-dao-test/room)]
         (roomgroup {:room_id room_id})
         (roomgroup {:room_id room_id})
         => (throws clojure.lang.ExceptionInfo "Duplicate name")))
      (fact "Tree structure with parent"
        (let [{room_id :id} (room-dao-test/room)
              {:keys [id]} (roomgroup (roomgroup-data {:room_id room_id}))]
          (roomgroup {:room_id room_id
                      :name "Room group child"
                      :parent id}) => (roomgroup-child-expected id)))))
  (facts "Dao: roomgroup delete"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (let [{room_id :id} (room-dao-test/room)
              {roomgroup_id :id} (roomgroup {:room_id room_id})]
          (dao/delete! db-spec {:id roomgroup_id}))))))