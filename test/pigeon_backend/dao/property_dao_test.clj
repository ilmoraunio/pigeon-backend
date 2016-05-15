(ns pigeon-backend.dao.property-dao-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.dao.property-dao :as dao]
            [pigeon-backend.dao.dao-mother :as dao-mother]
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

(deftest property-dao-test
  (facts "Dao: property create"
    (with-state-changes [(before :facts (empty-and-create-tables))] 
      (fact "Basic case"
        (let [directedconnection (dao-mother/directedconnection)]
          (dao/create! db-spec (property-data (:id directedconnection)))
            => (property-data-expected (:id directedconnection))))
      (fact "Duplicate ok"
        (let [directedconnection (dao-mother/directedconnection)]
          (dao/create! db-spec (property-data (:id directedconnection)))
          (dao/create! db-spec (property-data (:id directedconnection)))
            => (property-data-expected (:id directedconnection)))))))