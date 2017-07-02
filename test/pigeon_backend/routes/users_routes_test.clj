(ns pigeon-backend.routes.login-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [pigeon-backend.db.migrations :as migrations]
            [midje.sweet :refer :all]
            [schema.core :as s]))

(deftest users-routes-test
  (with-state-changes [(before :facts (empty-and-create-tables))]
    (fact
      (let [account1 {:username "foo"
                      :password "hunter2"
                      :name "name"}
            account2 {:username "bar"
                      :password "hunter2"
                      :name "name"}
            _        (new-account account1)
            _        (new-account account2)
            response (app (-> (mock/request :get "/api/v0/users/foo")
                              (mock/content-type "application/json")))
            body     (parse-body (:body response))]
        (:status response) => 200
        body => (one-of coll?)
        body => (contains [(contains {:username "bar"})]))))
  (with-state-changes [(before :facts (do (empty-and-create-tables)
                                          (migrations/migrate-data-extra)))]
    (fact
      (let [response (app (-> (mock/request :get "/api/v0/users/team_1_player_1")
                            (mock/content-type "application/json")))
            body     (parse-body (:body response))]
        (:status response) => 200
        body => (n-of coll? 3)
        body => (contains [(contains {:username "team_1_supreme_commander"}
                                     {:username "team_1_player_2"}
                                     {:username "team_1_player_3"})])))))