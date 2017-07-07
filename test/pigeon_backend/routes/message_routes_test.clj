(ns pigeon-backend.routes.message-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [midje.sweet :refer :all]
            [schema.core :as s]
            [pigeon-backend.db.migrations :as migrations]))

(deftest message-routes-test
  (with-state-changes [(before :facts (do (empty-and-create-tables)
                                          ;; todo: fix tests to be deterministic
                                          (migrations/migrate-data-extra)))]
    (fact "Insertion"
      (let [response (app (-> (mock/request :post "/api/v0/message/sender/team_1_supreme_commander/recipient/team_1_player_1")
                              (mock/content-type "application/json")
                              (mock/body (cheshire/generate-string {:message "message"}))))
            body     (parse-body (:body response))]
        (:status response) => 200
        (:message body) => "message"))

    (fact "Insertion (send_limit, separate multiple)"
      (defn insert-request [recipient] (app (-> (mock/request :post (str "/api/v0/message/sender/team_1_supreme_commander/recipient/" recipient))
                                              (mock/content-type "application/json")
                                              (mock/body (cheshire/generate-string {:message "message"})))))
      (let [response-1 (insert-request "team_1_player_1")
            response-2 (insert-request "team_1_player_2")
            body     (parse-body (:body response-2))]
        (:status response-1) => 200
        (:status response-2) => 200))

    (fact "Message quota exceeded (send_limit)"
      (defn insert-request [] (app (-> (mock/request :post "/api/v0/message/sender/team_1_supreme_commander/recipient/team_1_player_1")
                                       (mock/content-type "application/json")
                                       (mock/body (cheshire/generate-string {:message "message"})))))
      (let [response-1 (insert-request)
            response-2 (insert-request)
            body     (parse-body (:body response-2))]
        (:status response-1) => 200
        (:status response-2) => 400))

    (comment (fact "Message quota exceeded (shared_send_limit)"
               (let [response-1 (app (-> (mock/request :post "/api/v0/message/sender/team_1_player_1/recipient/team_1_player_2")
                                       (mock/content-type "application/json")
                                       (mock/body (cheshire/generate-string {:message "message"}))))
                     response-2 (app (-> (mock/request :post "/api/v0/message/sender/team_1_player_1/recipient/team_1_player_3")
                                       (mock/content-type "application/json")
                                       (mock/body (cheshire/generate-string {:message "message"}))))
                     body     (parse-body (:body response-2))]
                 (:status response-1) => 200
                 (:status response-2) => 400)))

    (fact "Listing"
      (let [account1  {:username "foo"
                       :password "hunter2"
                       :name "name"}
            account2  {:username "bar"
                       :password "hunter2"
                       :name "name"}
            _        (new-account account1)
            _        (new-account account2)
            _        (new-message {:sender "foo" :recipient "bar"})
            _        (new-message {:sender "bar" :recipient "foo"})
            response (app (-> (mock/request :get "/api/v0/message/sender/foo/recipient/bar")
                              (mock/content-type "application/json")))
            body     (parse-body (:body response))]
        (:status response) => 200
        body => two-of))))