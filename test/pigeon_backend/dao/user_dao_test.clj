(ns pigeon-backend.dao.user-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.user-dao :as dao]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]])
  (import java.sql.BatchUpdateException))


(def user-data {:username "foobar"
               :full_name "Foo Bar"
               :password "hunter2"})

(def user-data-expected (contains {:id integer?} 
                                 {:username "foobar"}
                                 {:full_name "Foo Bar"}
                                 {:password "hunter2"}
                                 {:created #(instance? java.util.Date %)}
                                 {:updated #(instance? java.util.Date %)}
                                 {:version 0}
                                 {:deleted false}))

(defn user
  ([] (let [data user-data]
        (dao/create! db-spec data)))

  ([{username :username :as input}]
      (let [data (assoc user-data :username username)]
        (dao/create! db-spec data))))

(deftest user-dao-test
  (facts "Dao: user create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (user) => user-data-expected)
      (fact "Duplicate username entry not allowed"
        (user)
        (user) => (throws clojure.lang.ExceptionInfo 
                                                  "Duplicate username"))))
  (facts "Dao: get by username"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (user)
        (user {:username "barfoo"})
        (dao/get-by db-spec {:username "foobar"})
          => (contains [(contains {:username "foobar"})]))))
  (facts "Dao: user update"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (let [{id :id} (user)]
          (dao/update! db-spec {:id id
                                :username "barfoo"
                                :full_name "Bar Foo"
                                :password "hunter2"}) 
            => (contains {:username "barfoo"}))))))