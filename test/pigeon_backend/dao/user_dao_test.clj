(ns pigeon-backend.dao.user-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.user-dao :as dao]
            [schema.core :as s]
            [pigeon-backend.test-util :refer [drop-and-create-tables]])
  (import java.sql.BatchUpdateException))

(def user-dto {:username "foobar"
               :full_name "Foo Bar"
               :password "hunter2"})

(deftest user-crud
  (facts "Create"
    (with-state-changes [(before :facts (drop-and-create-tables))]
      (fact "Basic case"
        (dao/create! user-dto) => true)
      (fact "Duplicate username entry not allowed"
        (dao/create! user-dto)
        (dao/create! user-dto) => (throws #"User.*?already exists")))))