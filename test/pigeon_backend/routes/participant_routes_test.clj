(ns pigeon-backend.routes.participant-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [midje.sweet :refer :all]
            [schema.core :as s]))

(deftest participant-routes-test
  (facts
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Join a room"
        (let [_           (new-account)
              room        (parse-body (:body (new-room)))
              participant {:room_id  (:id room)
                           :name "Participant!"
                           :username "Username!"}
              response    (new-participant participant)
              body        (parse-body (:body response))]
          (:status response) => 200
          body => (contains {:id string?}
                            {:room_id string?}
                            {:username string?})))
      (fact "List all participants for room"
          (let [_              (new-account)
                {room-id1 :id} (parse-body (:body (new-room)))
                {room-id2 :id} (parse-body (:body (new-room {:name "Pigeon room 2"})))
                _ (new-participant {:room_id room-id1
                                    :name "Participant 1"
                                    :username "Username!"})
                _ (new-participant {:room_id room-id1
                                    :name "Participant 2"
                                    :username "Username!"})
                _ (new-participant {:room_id room-id2
                                    :name "Participant 3"
                                    :username "Username!"})
                response (app (-> (mock/request :get (str "/api/v0/participant?room_id=" room-id1))
                                  (mock/content-type "application/json")
                                  (mock/header "Authorization" (str "Bearer " (create-test-login-token)))))
                body (parse-body (:body response))]
            (:status response) => 200
            body => (two-of coll?))))))
