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
  ([{:keys [room_id name users_id]
     :or {name "Room group" users_id 1}}]
   {:room_id room_id
    :name name
    :users_id users_id}))

(def participant-expected (contains {:id integer?}
                                  {:room_id integer?}
                                  {:name "Room group"}
                                  {:users_id 1}
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
            (participant tx {:room_id 1}) => participant-expected)))
      (fact "Duplicate group name inside room not allowed"
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (participant tx {:room_id 1})
            (participant tx {:room_id 1})) => (throws clojure.lang.ExceptionInfo "Duplicate name")))))
  (facts "Dao: participant get"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (participant tx {:room_id 1})
            (dao/get-by tx {:room_id 1}) => (contains [participant-expected]))))
      (fact "Multiple of same"
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (dotimes [n 2]
              (participant tx {:room_id 1
                             :name (str "foobar" n)}))
            (dao/get-by tx nil) => (two-of coll?))))
      (fact "Filtering"
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (participant tx {:room_id 1})
            (participant tx {:room_id 2})
            (dao/get-by tx {:room_id 1}) => (contains [(contains {:room_id 1})]))))))
  (facts "Dao: participant update"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (let [created-participant (participant tx {:room_id 1})
                  edited-participant (assoc created-participant :name "Edited room group")]
              (dao/update! tx edited-participant) => (contains {:name "Edited room group"})))))))
  (facts "Dao: participant delete"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (jdbc/with-db-transaction [tx db-spec]
          (without-fk-constraints tx
            (let [{participant_id :id} (participant tx {:room_id 1})]
              (dao/delete! tx {:id participant_id}) => (contains {:deleted true}
                                                               {:users_id 1}))))))))