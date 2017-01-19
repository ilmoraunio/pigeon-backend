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
          body => (contains {:id integer?}
                            {:room_id integer?}
                            {:users_id integer?}))))))