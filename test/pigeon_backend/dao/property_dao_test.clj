(ns pigeon-backend.dao.property-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.property-dao :as dao]
            [pigeon-backend.dao.directedconnection-dao-test :as directedconnection-dao-test]
            [schema.core :as s]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.test-util :refer [empty-and-create-tables]]))

(defn property-data [directedconnection-id]
  {:directedconnection_id directedconnection-id
   :name "property name"
   :type "property type"
   :value "property value"})

(defn property-data-expected [directedconnection-id]
  (contains {:id integer?}
            {:directedconnection_id directedconnection-id}
            {:name "property name"}
            {:type "property type"}
            {:value "property value"}
            {:created #(instance? java.util.Date %)}
            {:updated #(instance? java.util.Date %)}
            {:version 0}
            {:deleted false}))

(defn property
  ([] (let [directedconnection (directedconnection-dao-test/directedconnection)
            data (property-data (:id directedconnection))]
        (property data)))
  ([data] (dao/create! db-spec data)))

(deftest property-dao-test
  (facts "Dao: property create"
    (with-state-changes [(before :facts (empty-and-create-tables))] 
      (fact "Basic case"
        (let [directedconnection (directedconnection-dao-test/directedconnection)]
          (property (property-data (:id directedconnection)))
            => (property-data-expected (:id directedconnection))))
      (fact "Duplicate ok"
        (let [directedconnection (directedconnection-dao-test/directedconnection)]
          (property (property-data (:id directedconnection)))
          (property (property-data (:id directedconnection)))
            => (property-data-expected (:id directedconnection)))))))