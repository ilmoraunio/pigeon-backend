(ns pigeon-backend.dao.outcome-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.outcome-dao :as dao]
            [pigeon-backend.dao.property-dao-test :as property-dao-test]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(defn outcome-data [property-id]
  {:property_id property-id
   :type "outcome type"
   :value "outcome value"})

(defn outcome-data-expected [property-id]
  (contains {:id integer?}
            {:property_id property-id}
            {:type "outcome type"}
            {:value "outcome value"}
            {:created #(instance? java.util.Date %)}
            {:updated #(instance? java.util.Date %)}
            {:version 0}
            {:deleted false}))

(deftest outcome-dao-test
  (facts "Dao: outcome create"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Basic case"
        (let [property (property-dao-test/property)]
          (dao/create! db-spec (outcome-data (:id property)))
            => (outcome-data-expected (:id property))))
      (fact "Allow duplicates"
        (let [property (property-dao-test/property)]
          (dao/create! db-spec (outcome-data (:id property)))
            => (outcome-data-expected (:id property)))))))