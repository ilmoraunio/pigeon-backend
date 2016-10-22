(ns pigeon-backend.dao.roomgroup-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.roomgroup-dao :as dao]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables
                                              without-fk-constraints]]
            [clojure.java.jdbc :as jdbc]))

(defn roomgroup-data
  ([{:keys [room_id name users_id parent]
     :or {name "Room group" users_id 1 parent nil}}]
   {:room_id room_id
    :name name
    :parent parent ;; TODO: remove parent key from model
    :users_id users_id}))

(def roomgroup-expected (contains {:id integer?}
                                  {:room_id integer?}
                                  {:name "Room group"}
                                  {:parent nil}
                                  {:users_id 1}
                                  {:created #(instance? java.util.Date %)}
                                  {:updated #(instance? java.util.Date %)}
                                  {:version 0}
                                  {:deleted false}))

(defn roomgroup
  ([input] (roomgroup db-spec input))
  ([tx input] (dao/create! tx (roomgroup-data input))))

(deftest roomgroup-dao-test
  (facts "Dao: roomgroup create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (roomgroup tx {:room_id 1}) => roomgroup-expected)))
      (fact "Duplicate group name inside room not allowed"
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (roomgroup tx {:room_id 1})
            (roomgroup tx {:room_id 1})) => (throws clojure.lang.ExceptionInfo "Duplicate name")))))
  (facts "Dao: roomgroup get"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (roomgroup tx {:room_id 1})
            (dao/get-by tx {:room_id 1}) => (contains [roomgroup-expected]))))
      (fact "Multiple of same"
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (dotimes [n 2]
              (roomgroup tx {:room_id 1
                             :name (str "foobar" n)}))
            (dao/get-by tx nil) => (two-of coll?))))
      (fact "Filtering"
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (roomgroup tx {:room_id 1})
            (roomgroup tx {:room_id 2})
            (dao/get-by tx {:room_id 1}) => (contains [(contains {:room_id 1})]))))))
  (facts "Dao: roomgroup update"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (let [created-roomgroup (roomgroup tx {:room_id 1})
                  edited-roomgroup (assoc created-roomgroup :name "Edited room group")]
              (dao/update! tx edited-roomgroup) => (contains {:name "Edited room group"})))))))
  (facts "Dao: roomgroup delete"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (let [{roomgroup_id :id} (roomgroup tx {:room_id 1})]
              (dao/delete! tx {:id roomgroup_id}) => (contains {:deleted true}
                                                               {:users_id 1}))))))))