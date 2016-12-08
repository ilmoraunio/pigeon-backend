(ns pigeon-backend.routes.participant-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [midje.sweet :refer :all]
            [schema.core :as s]
            [pigeon-backend.routes.room-routes-test :refer [new-room]]
            [pigeon-backend.routes.registration-routes-test :refer [new-account]]))

(defn new-participant
  ([input] (app (-> (mock/request :post "/api/v0/participant")
                    (mock/content-type "application/json")
                    (mock/header "Authorization" (str "Bearer " (create-test-login-token)))
                    (mock/body (cheshire/generate-string input))))))

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