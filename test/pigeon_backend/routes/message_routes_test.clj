(ns pigeon-backend.routes.message-routes-test
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
      (fact "Post a message"
        (let [_           (new-account)
              _           (new-account {:username "Username2"
                                        :password "hunter2"
                                        :full_name "Real name!"})
              room          (parse-body (:body (new-room)))
              participant-1 (parse-body (:body (new-participant {:room_id  (:id room)
                                                                 :name "Participant!"
                                                                 :username "Username!"})))
              participant-2 (parse-body (:body (new-participant {:room_id  (:id room)
                                                                 :name "Participant2!"
                                                                 :username "Username2"})))
              response (new-message {:room_id (:id room)
                                     :sender (:id participant-1)
                                     :recipient (:id participant-2)
                                     :message "Hello world!"})
              body (parse-body (:body response))]
          (:status response) => 200
          body => (contains {:id string?}
                            {:message string?})))
      (fact "List a conversation"
        (let [_           (new-account)
              _           (new-account {:username "Username2"
                                        :password "hunter2"
                                        :full_name "Real name!"})
              room          (parse-body (:body (new-room)))
              participant-1 (parse-body (:body (new-participant {:room_id  (:id room)
                                                                 :name "Participant!"
                                                                 :username "Username!"})))
              participant-2 (parse-body (:body (new-participant {:room_id  (:id room)
                                                                 :name "Participant2!"
                                                                 :username "Username2"})))
              _ (new-message {:room_id (:id room)
                              :sender (:id participant-1)
                              :recipient (:id participant-2)
                              :message "Hello world!"})
              _ (new-message {:room_id (:id room)
                              :sender (:id participant-2)
                              :recipient (:id participant-1)
                              :message "Hello world2!"} (create-test-login-token "Username2"))
              response (app (-> (mock/request :get (str "/api/v0/message?room_id=" (:id room)
                                                        "&sender=" (:id participant-1)
                                                        "&recipient=" (:id participant-2)))
                                (mock/content-type "application/json")
                                (mock/header "Authorization" (str "Bearer " (create-test-login-token)))))
              body (parse-body (:body response))]
          (:status response) => 200
          body => (two-of coll?))))))
