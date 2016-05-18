(ns pigeon-backend.dao.room-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao :as dao]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(def room-dto {:name "Pigeon room"})

(def room-expected (contains {:name "Pigeon room"}
                             {:created #(instance? java.util.Date %)}
                             {:updated #(instance? java.util.Date %)}
                             {:version 0}
                             {:deleted false}))

(defn room
  ([] (let [data room-dto]
        (dao/create! db-spec data))))

(deftest room-dao-test
  (facts "Dao: room create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (room) => room-expected)
      (fact "Duplicate room name not allowed"
        (room) => irrelevant
        (room) => (throws clojure.lang.ExceptionInfo
                                            "Duplicate name")))))