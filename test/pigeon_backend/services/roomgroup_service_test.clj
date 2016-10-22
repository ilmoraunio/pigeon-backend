(ns pigeon-backend.services.roomgroup-service-test
  (:require [clojure.test :refer [deftest]]
            [pigeon-backend.migrations_test :refer [drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.db.migrations :as migrations]
            [midje.sweet :refer :all]
            [pigeon-backend.services.roomgroup-service :as service]
            [schema.core :as s]
            [pigeon-backend.test-util :refer [empty-and-create-tables
                                              fetch-input-schema-from-dao-fn]]
            [buddy.hashers :as hashers]
            [pigeon-backend.dao.roomgroup-dao :as roomgroup-dao]
            [schema-generators.generators :as g]
            [schema-generators.complete :as c])
  (import org.postgresql.util.PSQLException))

;; TODO: rename roomgroup to participant
(deftest roomgroup-service-crud
  (comment
    (facts "Roomgroup service: create"
      (with-state-changes [(before :facts (empty-and-create-tables))]
        (fact
          (let [input (g/generate (fetch-input-schema-from-dao-fn #'roomgroup-dao/create!))
                output (c/complete input roomgroup-dao/Model)
                expected output]
            (with-redefs [roomgroup-dao/create! (fn [_ _] output)]
              (service/roomgroup-create! input) => expected)))))
    (facts "Roomgroup service: read"
      (with-state-changes [(before :facts (empty-and-create-tables))]
        (fact
          (let [input nil
                output (g/generate roomgroup-dao/QueryResult)
                expected output]
            (with-redefs [roomgroup-dao/get-by (fn [_ _] output)]
              (service/roomgroup-get-by input) => expected)))))
    (facts "Roomgroup service: update"
      (with-state-changes [(before :facts (empty-and-create-tables))]
        (fact
          (let [input (g/generate (fetch-input-schema-from-dao-fn #'roomgroup-dao/update!))
                output (c/complete input roomgroup-dao/Model)
                expected output]
            (with-redefs [roomgroup-dao/update! (fn [_ _] output)]
              (service/roomgroup-update! input) => expected)))))
    (facts "Roomgroup service: delete"
      (with-state-changes [(before :facts (empty-and-create-tables))]
        (fact
          (let [input (g/generate (fetch-input-schema-from-dao-fn #'roomgroup-dao/delete!))
                output (c/complete input roomgroup-dao/Model)
                expected output]
            (with-redefs [roomgroup-dao/delete! (fn [_ _] output)]
              (service/roomgroup-delete! input) => expected))))))


  (facts "User should be able to add himself to room")
  (facts "User should be able to remove himself from room")
  (facts "User should be able to see all existing aliases in room")
  )
