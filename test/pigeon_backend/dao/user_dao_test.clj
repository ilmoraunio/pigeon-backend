(ns pigeon-backend.dao.user-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.user-dao :as dao]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]])
  (import java.sql.BatchUpdateException))

(def user-dto {:username "foobar"
               :full_name "Foo Bar"
               :password "hunter2"})

(def get-by-username-dto {:username "foobar"})

(def user-dto-expected (contains {:id integer?} 
                                 {:username "foobar"}
                                 {:full_name "Foo Bar"}
                                 {:password "hunter2"}
                                 {:created #(instance? java.util.Date %)}
                                 {:updated #(instance? java.util.Date %)}
                                 {:version 0}
                                 {:deleted false}))

(deftest user-dao-test
  (facts "Dao: user create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (dao/create! db-spec user-dto) => user-dto-expected)
      (fact "Duplicate username entry not allowed"
        (dao/create! db-spec user-dto)
        (dao/create! db-spec user-dto) => (throws clojure.lang.ExceptionInfo 
                                                  "Duplicate username"))))
  (facts "Dao: get by username"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (dao/create! db-spec user-dto)
        (dao/create! db-spec (assoc user-dto :username "barfoo"))
        (dao/get-by-username db-spec get-by-username-dto) 
          => user-dto-expected))))