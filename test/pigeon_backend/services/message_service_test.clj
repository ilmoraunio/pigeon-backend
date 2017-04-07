(ns pigeon-backend.services.message-service-test
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
                                              fetch-input-schema-from-dao-fn
                                              test-user]]
            [pigeon-backend.services.user-service :as user-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]
            [clojure.test :refer [deftest]]
            [pigeon-backend.migrations_test :refer [drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.db.migrations :as migrations]
            [midje.sweet :refer :all]
            [pigeon-backend.services.message-service :as service]
            [schema.core :as s]
            [buddy.hashers :as hashers]
            [pigeon-backend.dao.message :as message-db]
            [pigeon-backend.dao.model :as model]
            [schema-generators.generators :as g]
            [schema-generators.complete :as c]
            [pigeon-backend.services.room-service :as room-service]
            [pigeon-backend.dao.room-dao-test :as room-dao-test]
            [pigeon-backend.dao.participant-dao-test :as participant-dao-test]
            [pigeon-backend.util :as util]
            [pigeon-backend.dao.user-dao-test :as user-dao-test]))

(defn message-data
  ([{:keys [room_id sender recipient message]
     :or {message "foobar"}}]
   {:room_id room_id
    :sender sender
    :recipient recipient
    :message message}))

(defn message
  ([input] (message-db/create! db-spec (message-data input))))

(deftest message-test
  (facts "User should be able to add a message"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (let [user-1 test-user
              user-2 "foo2"
              _ (user-dao-test/user {:username user-1})
              _ (user-dao-test/user {:username user-2})
              {room-id :id} (room-dao-test/room)
              {participant-1 :id} (participant-dao-test/participant {:room_id  room-id
                                                                     :name     user-1
                                                                     :username user-1})
              {participant-2 :id} (participant-dao-test/participant {:room_id  room-id
                                                                     :name     user-2
                                                                     :username user-2})]
          (service/add-message! {:room_id   room-id
                                 :sender    participant-1
                                 :recipient participant-2
                                 :message "foobar"}
                                (create-test-login-token)) => (contains {:id model/id-pattern?})))))
  (facts "User should get a conversation (messages sent from self to another and vice versa)"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (let [user-1 test-user
              user-2 "foo2"
              _ (user-dao-test/user {:username user-1})
              _ (user-dao-test/user {:username user-2})
              {room-id :id} (room-dao-test/room)
              {participant-1 :id} (participant-dao-test/participant {:room_id  room-id
                                                                     :name     user-1
                                                                     :username user-1})
              {participant-2 :id} (participant-dao-test/participant {:room_id  room-id
                                                                     :name     user-2
                                                                     :username user-2})
              _ (message {:room_id   room-id
                          :sender    participant-1
                          :recipient participant-2
                          :message "foobar"})
              _ (message {:room_id   room-id
                          :sender    participant-2
                          :recipient participant-1
                          :message "foobar"})]
          (service/get-messages {:room_id room-id
                                 :sender participant-1
                                 :recipient participant-2}
                                (create-test-login-token)) => (two-of coll?))))))