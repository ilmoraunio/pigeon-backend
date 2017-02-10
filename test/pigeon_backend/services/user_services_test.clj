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
            [schema-generators.complete :as c]
            [pigeon-backend.dao.user-dao :as user-dao])
  (import org.postgresql.util.PSQLException))

(def user-dto {:username "foobar"
               :full_name "Foo Bar"
               :password "hunter2"})

(def expected-user-dto (contains {:username "foobar"}
                                 {:full_name "Foo Bar"}
                                 {:created #(instance? java.util.Date %)}
                                 {:updated #(instance? java.util.Date %)}
                                 {:version 0}
                                 {:deleted false}))

(def credentials-dto {:username "foobar" :password "hunter2"})
(def wrong-credentials-dto {:username "foobar" :password "password123"})

(deftest user-service-crud
  (facts "Service: user create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (let [returned-dto (service/user-create! user-dto)]
          returned-dto => expected-user-dto
          (:password returned-dto) => nil))
      (fact "Duplicate username entry not allowed"
        (service/user-create! user-dto)
        (service/user-create! user-dto) => (throws clojure.lang.ExceptionInfo
                                                   "Duplicate username"))))
  (facts "Service: user check credentials"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Success"
        (let [returned-dto (service/user-create! user-dto)]
          (service/check-credentials credentials-dto) => true))
      (fact "Unsuccess!"
        (let [returned-dto (service/user-create! user-dto)]
          (service/check-credentials wrong-credentials-dto) => false))))
  (facts "Get-by-username"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (let [username (g/generate String)
              output (c/complete {:username username} service/Model)
              expected output]
          (with-redefs [user-dao/get-by-username (fn [_ _] output)]
            (service/get-by-username username) => expected)))))
  (facts "Service: update user"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Success"
        (let [_ (service/user-create! user-dto)]
          (service/user-update! (assoc user-dto :full_name "Bar Foo"))
          => (contains {:full_name "Bar Foo"})))))
  (facts "Service: delete user"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Success"
        (let [_ (service/user-create! user-dto)]
          (service/user-delete! user-dto))))))