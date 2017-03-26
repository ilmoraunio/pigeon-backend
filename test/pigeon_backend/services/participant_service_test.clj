(ns pigeon-backend.services.participant-service-test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [pigeon-backend.test-util :refer :all]
            [pigeon-backend.services.user-service :as user-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]
            [clojure.test :refer [deftest]]
            [pigeon-backend.migrations_test :refer [drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.db.migrations :as migrations]
            [midje.sweet :refer :all]
            [pigeon-backend.services.participant-service :as service]
            [schema.core :as s]
            [buddy.hashers :as hashers]
            [pigeon-backend.dao.participant-dao :as participant-dao]
            [schema-generators.generators :as g]
            [schema-generators.complete :as c]
            [pigeon-backend.services.room-service :as room-service]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.participant-dao-test :as participant-dao-test]
            [pigeon-backend.util :as util]
            [pigeon-backend.dao.user-dao-test :as user-dao-test]))

;; todo: get rid of mocking service tests and do proper integration tests instead

(deftest participant-test
  (facts "User should be able to add himself to room"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (let [_ (user-dao-test/user)
              {room-id :id} (room-dao-test/room)]
          (service/add-participant! {:room_id room-id
                                     :name test-user
                                     :username test-user}) => (contains {:id string?})))))
  (facts "User should be able to list all participants in a room"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (let [_ (user-dao-test/user)
              {other-user :username} (user-dao-test/user {:username "Username2"})
              {room-id :id} (room-dao-test/room)
              _ (participant-dao-test/participant {:room_id room-id
                                                   :name     test-user
                                                   :username test-user})
              _ (participant-dao-test/participant {:room_id room-id
                                                   :name     other-user
                                                   :username other-user})]
          (service/get-by-room room-id (create-test-login-token)) => (two-of coll?)))))
  (facts "Simple authorization"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Doesn't authorize"
        (with-redefs [participant-dao/get-auth (fn [_ _] false)]
          (service/authorize anything (create-test-login-token)) => (throws Exception)))
      (fact "Authorizes"
        (with-redefs [participant-dao/get-auth (fn [_ _] true)]
          (service/authorize anything (create-test-login-token)) => nil))))
  (facts "Authorization against room & participant"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Does not authorize"
        (let [_ (user-dao-test/user)
              {other-user :username} (user-dao-test/user {:username "Username2"})
              {room-id :id} (room-dao-test/room)
              {other-room-id :id} (room-dao-test/room {:name "Pigeon room 2"})
              _ (participant-dao-test/participant {:room_id  room-id
                                                   :name     test-user
                                                   :username test-user})
              {recipient-id :id} (participant-dao-test/participant {:room_id  other-room-id
                                                                    :name     other-user
                                                                    :username other-user})]
          (service/authorize-by-participant room-id recipient-id (create-test-login-token)) => (throws Exception)))
      (fact "Authorizes"
        (let [_ (user-dao-test/user)
              {other-user :username} (user-dao-test/user {:username "Username2"})
              {room-id :id} (room-dao-test/room)
              _ (participant-dao-test/participant {:room_id  room-id
                                                   :name     test-user
                                                   :username test-user})
              {recipient-id :id} (participant-dao-test/participant {:room_id  room-id
                                                                    :name     other-user
                                                                    :username other-user})]
          (service/authorize-by-participant room-id recipient-id (create-test-login-token)) => nil)))))