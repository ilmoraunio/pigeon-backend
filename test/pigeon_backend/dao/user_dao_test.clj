(ns pigeon-backend.dao.user-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.user-dao :as dao]
            [schema.core :as s])
  (import java.sql.BatchUpdateException))

(def user-dto {:username "foobar"
               :full_name "Foo Bar"
               :password "hunter2"})

(deftest user-crud
  (facts "Create"
    (with-state-changes [(before :facts (drop-and-create-tables))]
      (fact "Basic case"
        (dao/user-create user-dto) => true)
      (fact "PSQLException failure"
        (dao/user-create user-dto)
        (dao/user-create user-dto) => (throws BatchUpdateException)))))