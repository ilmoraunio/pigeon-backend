(ns pigeon-backend.dao.groupuser-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.roomgroup-dao :as roomgroup-dao]
            [pigeon-backend.dao.user-dao-test :as user-dao-test]
            [pigeon-backend.dao.room-dao-test :refer [room-dto]]
            [pigeon-backend.dao.roomgroup-dao-test :refer [roomgroup-dto]]
            [pigeon-backend.dao.user-dao :as user-dao]
            [pigeon-backend.dao.groupuser-dao :as dao]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(defn groupuser-dto [roomgroup_id userid]
  {:roomgroup_id roomgroup_id
   :users_id userid})

(defn groupuser-expected [userid]
  (contains {:roomgroup_id "Pigeon room"}
            {:users_id userid}
            {:created #(instance? java.util.Date %)}
            {:updated #(instance? java.util.Date %)}
            {:version 0}
            {:deleted false}))

(deftest groupuser-dao-test
  (facts "Dao: groupuser create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (let [{userid :id} (user-dao-test/user)
              _ (room-dao-test/room)
              {roomgroup_id :id} (roomgroup-dao/create! db-spec roomgroup-dto)]
          (dao/create! db-spec (groupuser-dto roomgroup_id userid))
            => groupuser-expected))
      (fact "Duplicate groupuser inside room not allowed"
        (let [{userid :id} (user-dao-test/user)
              _ (room-dao-test/room)
              {roomgroup_id :id} (roomgroup-dao/create! db-spec roomgroup-dto)]
          (dao/create! db-spec (groupuser-dto roomgroup_id userid))
          (dao/create! db-spec (groupuser-dto roomgroup_id userid))
            => (throws clojure.lang.ExceptionInfo
                "Duplicate group user"))))))
