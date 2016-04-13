(ns pigeon-backend.services.user-services-test
  (:require [clojure.test :refer [deftest]]
            [pigeon-backend.migrations_test :refer [drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.db.migrations :as migrations]
            [midje.sweet :refer :all]
            [pigeon-backend.services.user-service :as service]
            [schema.core :as s]
            [pigeon-backend.test-util :refer [drop-and-create-tables]]
            [buddy.hashers :as hashers])
  (import org.postgresql.util.PSQLException))

(def user-dto {:username "foobar"
               :full_name "Foo Bar"
               :password "hunter2"})

(def expected-user-dto (contains {:username "foobar"}
                                 {:full_name "Foo Bar"}
                                 {:password #"^bcrypt\+sha512\$.{84}"}))

(def credentials-dto {:username "foobar" :password "hunter2"})
(def wrong-credentials-dto {:username "foobar" :password "password123"})

(deftest user-service-crud
  (facts "Service: user create"
    (with-state-changes [(before :facts (drop-and-create-tables))]
      (fact "Basic case"
        (let [returned-dto (service/user-create! user-dto)]
          returned-dto => expected-user-dto
          (hashers/check "hunter2" (:password returned-dto)) => true))
      (fact "Duplicate username entry not allowed"
        (service/user-create! user-dto)
        (service/user-create! user-dto) => (throws clojure.lang.ExceptionInfo
                                                   "Duplicate username"))))
  (facts "Service: user check credentials"
    (with-state-changes [(before :facts (drop-and-create-tables))]
      (fact "Success"
        (let [returned-dto (service/user-create! user-dto)]
          (service/check-credentials credentials-dto) => true))
      (fact "Unsuccess!"
        (let [returned-dto (service/user-create! user-dto)]
          (service/check-credentials wrong-credentials-dto) => false)))))