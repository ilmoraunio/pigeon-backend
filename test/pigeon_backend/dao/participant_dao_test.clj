(ns pigeon-backend.dao.participant-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.participant-dao :as dao]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables
                                              without-fk-constraints]]
            [clojure.java.jdbc :as jdbc]))

(defn participant-data
  ([{:keys [room_id name username]
     :or {name "Room group" username "foobar"}}]
   {:room_id room_id
    :name name
    :username username}))

(def participant-expected (contains {:id string?}
                                  {:room_id string?}
                                  {:name "Room group"}
                                  {:username "foobar"}
                                  {:created #(instance? java.util.Date %)}
                                  {:updated #(instance? java.util.Date %)}
                                  {:version 0}
                                  {:deleted false}))

(defn participant
  ([input] (participant db-spec input))
  ([tx input] (dao/create! tx (participant-data input))))

(deftest participant-dao-test
  (facts "Dao: participant create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (participant tx {:room_id "id-1"}) => participant-expected)))
      (fact "Duplicate group name inside room not allowed"
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (participant tx {:room_id "id-1"})
            (participant tx {:room_id "id-1"})) => (throws clojure.lang.ExceptionInfo "Duplicate name")))))
  (facts "Dao: participant get"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (participant tx {:room_id "id-1"})
            (dao/get-by tx {:room_id "id-1"}) => (contains [participant-expected]))))
      (fact "Multiple of same"
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (dotimes [n 2]
              (participant tx {:room_id "id-1"
                             :name (str "foobar" n)}))
            (dao/get-by tx nil) => (two-of coll?))))
      (fact "Multiple from same room (excluding rest)"
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (participant tx {:room_id "id-1"
                             :name "foobar 1"})
            (participant tx {:room_id "id-1"
                             :name "foobar 2"})
            (participant tx {:room_id "id-2"
                             :name "foobar 3"})
            (dao/get-by tx {:room_id "id-1"}) => (two-of coll?))))
      (fact "Filtering"
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (participant tx {:room_id "id-1"})
            (participant tx {:room_id "id-2"})
            (dao/get-by tx {:room_id "id-1"}) => (contains [(contains {:room_id "id-1"})]))))))
  (facts "Simple authentication"
      (with-state-changes [(before :facts (empty-and-create-tables))]
        (fact "Doesn't authenticate"
          (jdbc/with-db-transaction [tx db-spec]
            (without-fk-constraints tx
              (let [{username :username} (pigeon-backend.dao.user-dao-test/user)
                    {room-id :id} (pigeon-backend.dao.room-dao-test/room)]
                (dao/get-auth tx {:room_id room-id :username "foobar"}) => false))))
        (fact "Authenticates"
          (jdbc/with-db-transaction [tx db-spec]
            (without-fk-constraints tx
              (let [{username :username} (pigeon-backend.dao.user-dao-test/user)
                    {room-id :id} (pigeon-backend.dao.room-dao-test/room)
                    _ (pigeon-backend.dao.participant-dao-test/participant {:room_id room-id
                                                                          :name "participant name"
                                                                          :username "foobar"})]
                (dao/get-auth tx {:room_id room-id :username "foobar"}) => true)))))))