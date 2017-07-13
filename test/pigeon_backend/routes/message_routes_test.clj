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
                              (mock/body (cheshire/generate-string {:message "message"}))))]
        (:status response) => 201))

    (fact "Insertion (send_limit, separate multiple)"
      (defn insert-request [recipient] (app (-> (mock/request :post (str "/api/v0/message/sender/team_1_supreme_commander/recipient/" recipient))
                                                (mock/content-type "application/json")
                                                (mock/body (cheshire/generate-string {:message "message"})))))
      (let [response-1 (insert-request "team_1_player_1")
            response-2 (insert-request "team_1_player_2")]
        (:status response-1) => 201
        (:status response-2) => 201))

    (fact "Message quota exceeded (send_limit)"
      (defn insert-request [] (app (-> (mock/request :post "/api/v0/message/sender/team_1_supreme_commander/recipient/team_1_player_1")
                                       (mock/content-type "application/json")
                                       (mock/body (cheshire/generate-string {:message "message"})))))
      (let [response-1 (insert-request)
            response-2 (insert-request)]
        (:status response-1) => 201
        (:status response-2) => 400))

    (fact "Message quota exceeded (shared_send_limit)"
      (let [response-1 (app (-> (mock/request :post "/api/v0/message/sender/team_1_player_1/recipient/team_1_player_2")
                                (mock/content-type "application/json")
                                (mock/body (cheshire/generate-string {:message "message"}))))
            response-2 (app (-> (mock/request :post "/api/v0/message/sender/team_1_player_1/recipient/team_1_player_3")
                                (mock/content-type "application/json")
                                (mock/body (cheshire/generate-string {:message "message"}))))]
        (:status response-1) => 201
        (:status response-2) => 400))

    (fact "Listing"
      (let [_ (app (-> (mock/request :post "/api/v0/message/sender/team_1_supreme_commander/recipient/team_1_player_1")
                       (mock/content-type "application/json")
                       (mock/body (cheshire/generate-string {:message "message"}))))
            response (app (-> (mock/request :get "/api/v0/message/sender/team_1_supreme_commander/recipient/team_1_player_1")
                              (mock/content-type "application/json")))
            body     (parse-body (:body response))]
        (:status response) => 200
        body => (one-of coll?)))

    ;; todo: w/ moderator powers only
    (fact "Delete message"
      (let [_ (app (-> (mock/request :post "/api/v0/message/sender/team_1_supreme_commander/recipient/team_1_player_1")
                       (mock/content-type "application/json")
                       (mock/body (cheshire/generate-string {:message "message"}))))
            [{id :id} & _]   (parse-body (:body (app (-> (mock/request :get "/api/v0/message/sender/team_1_supreme_commander/recipient/team_1_player_1")
                                                   (mock/content-type "application/json")))))
            response-delete (app (-> (mock/request :delete (str "/api/v0/message/" id))
                                     (mock/content-type "application/json")))
            messages (parse-body (:body (app (-> (mock/request :get "/api/v0/message/sender/team_1_supreme_commander/recipient/team_1_player_1")
                                                 (mock/content-type "application/json")))))]
        (:status response-delete) => 204
        messages => empty?))

    ;; todo w/ moderator powers only
    (fact "Undelete message"
      (let [_ (app (-> (mock/request :post "/api/v0/message/sender/team_1_supreme_commander/recipient/team_1_player_1")
                       (mock/content-type "application/json")
                       (mock/body (cheshire/generate-string {:message "message"}))))
            [{id :id} & _]   (parse-body (:body (app (-> (mock/request :get "/api/v0/message/sender/team_1_supreme_commander/recipient/team_1_player_1")
                                                         (mock/content-type "application/json")))))
            _ (app (-> (mock/request :delete (str "/api/v0/message/" id))
                       (mock/content-type "application/json")))
            response (app (-> (mock/request :patch (str "/api/v0/message/" id))
                              (mock/content-type "application/json")))
            messages-2 (parse-body (:body (app (-> (mock/request :get "/api/v0/message/sender/team_1_supreme_commander/recipient/team_1_player_1")
                                                   (mock/content-type "application/json")))))]
        (:status response) => 204
        messages-2         => (one-of coll?)))))