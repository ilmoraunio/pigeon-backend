(ns pigeon-backend.services.user-services-test
  (:require [clojure.test :refer [deftest]]
            [pigeon-backend.migrations_test :refer [drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.db.migrations :as migrations]
            [midje.sweet :refer :all]
            [pigeon-backend.services.user-service :as service]
            [schema.core :as s]
            [pigeon-backend.test-util :refer [drop-and-create-tables]])
  (import org.postgresql.util.PSQLException))

(def user-dto {:username "foobar"
               :full_name "Foo Bar"
               :password "hunter2"})

(deftest user-service-crud
  (facts "Create"
    (with-state-changes [(before :facts (drop-and-create-tables))]
      (fact "Basic case"
        (service/user-create! user-dto) => true)
      (fact "Duplicate username entry not allowed"
        (service/user-create! user-dto)
        (service/user-create! user-dto) => {:errors {:status 400 
                                                     :title "Invalid username" 
                                                     :detail "User foobar already exists"}}))))