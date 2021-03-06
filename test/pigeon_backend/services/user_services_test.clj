(ns pigeon-backend.services.user-services-test
  (:require [clojure.test :refer [deftest]]
            [pigeon-backend.migrations_test :refer [drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.db.migrations :as migrations]
            [midje.sweet :refer :all]
            [pigeon-backend.services.user-service :as service]
            [schema.core :as s]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]
            [buddy.hashers :as hashers]
            [schema-generators.generators :as g]
            [schema-generators.complete :as c])
  (import org.postgresql.util.PSQLException))

(def user-dto {:username "foobar"
               :name "Foo Bar"
               :password "hunter2"})

(def expected-user-dto (contains {:username "foobar"}
                                 {:name "Foo Bar"}
                                 {:created #(instance? java.util.Date %)}
                                 {:updated #(instance? java.util.Date %)}
                                 {:version 0}
                                 {:deleted false}))

(def credentials-dto {:username "foobar" :password "hunter2"})
(def wrong-credentials-dto {:username "foobar" :password "password123"})

(deftest user-service-crud
  (with-state-changes [(before :facts (empty-and-create-tables))]
    (fact
      (let [returned-dto (service/user-create! user-dto)]
        returned-dto => expected-user-dto
        (:password returned-dto) => nil))
    (fact "Duplicate username entry not allowed"
      (service/user-create! user-dto)
      (service/user-create! user-dto) => (throws org.postgresql.util.PSQLException)))
  (with-state-changes [(before :facts (empty-and-create-tables))]
    (fact "user check credentials - success"
      (let [returned-dto (service/user-create! user-dto)]
        (service/check-credentials credentials-dto) => true))
    (fact "user check credentials - unsuccesful"
      (let [returned-dto (service/user-create! user-dto)]
        (service/check-credentials wrong-credentials-dto) => false))))