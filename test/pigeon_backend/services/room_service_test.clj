(ns pigeon-backend.services.room-service-test
  (:require [clojure.test :refer [deftest]]
            [pigeon-backend.migrations_test :refer [drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.db.migrations :as migrations]
            [midje.sweet :refer :all]
            [pigeon-backend.services.room-service :as service]
            [schema.core :as s]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]
            [buddy.hashers :as hashers]
            [pigeon-backend.dao.room-dao-test :as room-dao])
  (import org.postgresql.util.PSQLException))

(def expected (contains {:name "Huone"}
                        {:created #(instance? java.util.Date %)}
                        {:updated #(instance? java.util.Date %)}
                        {:version 0}
                        {:deleted false}))

(deftest room-service-crud
  (facts "Service: room create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (let [input-data {:name "Huone"}
              output-data (service/room-create! input-data)]
          output-data => expected))
      (fact "Duplicate room entry not allowed"
        (let [input-data {:name "Huone"}]
          (service/room-create! input-data)
          (service/room-create! input-data) =>
            (throws clojure.lang.ExceptionInfo
              "Duplicate name")))))
  (facts "Service: room read"
    (with-state-changes [(before :facts (empty-and-create-tables))]
        (fact "Get one"
          (let [{id1 :id room-name-1 :name} (room-dao/room)]
            (service/room-get-by {:name room-name-1}) => (contains [(contains {:name room-name-1})])))
        (fact "Get multiple"
          (let [{id1 :id room-name-1 :name} (room-dao/room)
                {id2 :id room-name-2 :name} (room-dao/room {:name "Pigeon room 2"})]
            (service/room-get-by nil) => (two-of coll?)))
        (fact "Get none"
          (let [_ (room-dao/room)]
            (service/room-get-by {:name "wrong name"}) => [])))))