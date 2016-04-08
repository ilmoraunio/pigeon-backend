(ns pigeon-backend.dao.user-dao-test
  (:require [clojure.test :refer [deftest]]
            [pigeon-backend.migrations_test :refer [drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.db.migrations :as migrations]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.user-dao :as dao]
            [schema.core :as s]))

(defn drop-and-create-tables []
  (drop-all-tables db-spec)
  (migrations/migrate))

(def user-dto {:username "foobar"
               :full_name "Foo Bar"
               :password "hunter2"})

(deftest user-crud
  (facts "Create"
    (with-state-changes [(before :facts (drop-and-create-tables))]
      (fact "Basic case"
        (dao/create user-dto) => 1))))