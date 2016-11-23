(ns pigeon-backend.usecases.roomgroup-service-test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [pigeon-backend.test-util :refer [empty-and-create-tables
                                              parse-body
                                              create-login-token
                                              create-test-login-token
                                              clj-timestamp
                                              fetch-input-schema-from-dao-fn]]
            [pigeon-backend.services.user-service :as user-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]
            [clojure.test :refer [deftest]]
            [pigeon-backend.migrations_test :refer [drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.db.migrations :as migrations]
            [midje.sweet :refer :all]
            [pigeon-backend.services.roomgroup-service :as service]
            [schema.core :as s]
            [buddy.hashers :as hashers]
            [pigeon-backend.dao.roomgroup-dao :as roomgroup-dao]
            [schema-generators.generators :as g]
            [schema-generators.complete :as c]
            [pigeon-backend.services.room-service :as room-service]
            [pigeon-backend.routes.room-routes-test :as room-routes-test]))

(deftest roomgroup-test
  (facts "User should be able to add himself to room"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (let [input (g/generate service/AddRoomgroup)
              output (c/complete input service/Model)
              expected output]
          (with-redefs [roomgroup-dao/create! (fn [_ _] output)]
            (service/add-roomgroup! input) => expected)))))
  (facts "User should be able to remove himself from room")
  (facts "User should be able to see all existing aliases in room"))