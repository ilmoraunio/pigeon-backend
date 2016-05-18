(ns pigeon-backend.dao.groupuser-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.roomgroup-dao :as roomgroup-dao]
            [pigeon-backend.dao.user-dao-test :as user-dao-test]
            [pigeon-backend.dao.roomgroup-dao-test :as roomgroup-dao-test]
            [pigeon-backend.dao.user-dao :as user-dao]
            [pigeon-backend.dao.groupuser-dao :as dao]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(defn groupuser-data
  ([& {:keys [roomgroup_id users_id]}]
    {:roomgroup_id roomgroup_id
     :users_id users_id}))

(defn groupuser-expected [userid]
  (contains {:roomgroup_id "Pigeon room"}
            {:users_id userid}
            {:created #(instance? java.util.Date %)}
            {:updated #(instance? java.util.Date %)}
            {:version 0}
            {:deleted false}))

(defn groupuser
  ([data] (dao/create! db-spec data)))

(deftest groupuser-dao-test
  (facts "Dao: groupuser create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (let [{userid :id} (user-dao-test/user)
              _ (room-dao-test/room)
              {roomgroup_id :id} (roomgroup-dao-test/roomgroup)]
          (groupuser (groupuser-data :roomgroup_id roomgroup_id
                                     :users_id userid))
            => groupuser-expected))
      (fact "Duplicate groupuser inside room not allowed"
        (let [{userid :id} (user-dao-test/user)
              _ (room-dao-test/room)
              {roomgroup_id :id} (roomgroup-dao-test/roomgroup)]
          (groupuser (groupuser-data :roomgroup_id roomgroup_id
                                     :users_id userid))
          (groupuser (groupuser-data :roomgroup_id roomgroup_id
                                     :users_id userid))
            => (throws clojure.lang.ExceptionInfo
                "Duplicate group user"))))))
