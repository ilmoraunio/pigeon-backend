(ns pigeon-backend.services.participant-service-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [pigeon-backend.test-util :refer :all]
            [environ.core :refer [env]]
            [clojure.test :refer [deftest]]
            [pigeon-backend.migrations_test :refer [drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec]]
            [midje.sweet :refer :all]
            [pigeon-backend.services.participant-service :as service]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.participant-dao-test :as participant-dao-test]
            [pigeon-backend.dao.user-dao-test :as user-dao-test]))

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
        (let [_ (user-dao-test/user)
              {room-id :id} (room-dao-test/room)]
          (service/authorize room-id (create-test-login-token)) => (throws Exception)))
      (fact "Authorizes"
        (let [_ (user-dao-test/user)
              {room-id :id} (room-dao-test/room)
              _ (participant-dao-test/participant {:room_id  room-id
                                                   :name     test-user
                                                   :username test-user})]
          (service/authorize room-id (create-test-login-token)) => nil))))
  (facts "Authorization against room & participant"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Does not authorize"
        (let [_ (user-dao-test/user)
              {other-user :username} (user-dao-test/user {:username "Username2"})
              {room-id :id} (room-dao-test/room)
              {other-room-id :id} (room-dao-test/room {:name "Pigeon room 2"})
              {sender-id :id} (participant-dao-test/participant {:room_id  room-id
                                                   :name     test-user
                                                   :username test-user})
              {recipient-id :id} (participant-dao-test/participant {:room_id  other-room-id
                                                                    :name     other-user
                                                                    :username other-user})]
          (service/authorize-by-participant room-id sender-id recipient-id (create-test-login-token)) => (throws Exception)))
      (fact "Authorizes"
        (let [_ (user-dao-test/user)
              {other-user :username} (user-dao-test/user {:username "Username2"})
              {room-id :id} (room-dao-test/room)
              {sender-id :id} (participant-dao-test/participant {:room_id  room-id
                                                   :name     test-user
                                                   :username test-user})
              {recipient-id :id} (participant-dao-test/participant {:room_id  room-id
                                                                    :name     other-user
                                                                    :username other-user})]
          (service/authorize-by-participant room-id sender-id recipient-id (create-test-login-token)) => nil)))))