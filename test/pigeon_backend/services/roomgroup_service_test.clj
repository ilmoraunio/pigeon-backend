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

;; TODO: rename roomgroup to alias
(deftest roomgroup-service-crud
  (facts "Roomgroup service: create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (let [input (g/generate (fetch-input-schema-from-dao-fn 'pigeon-backend.dao.roomgroup-dao
                                                                #'roomgroup-dao/create!))
              output (c/complete input roomgroup-dao/Model)
              expected output]
          (with-redefs [roomgroup-dao/create! (fn [_ _] output)]
            (service/roomgroup-create! input) => expected))))))