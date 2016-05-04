(ns pigeon-backend.dao.room-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.room-dao :as dao]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(def room-dto {:name "Pigeon room"})

(deftest room-dao-test
  (facts "Dao: room create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (dao/create! db-spec room-dto) => room-dto))))