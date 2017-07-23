(ns pigeon-backend.routes.login-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [pigeon-backend.db.migrations :as migrations]
            [midje.sweet :refer :all]
            [schema.core :as s]))

(deftest login-routes-test
  (with-state-changes [(before :facts (do (empty-and-create-tables)
                                          (migrations/migrate-data-extra)))]
    (fact
      (let [response (app (-> (mock/request :post "/api/v0/session")
                              (mock/content-type "application/json")
                              (mock/body (cheshire/generate-string {:username "team_1_player_1"
                                                                    :password "hunter2"}))))
            body     (parse-body (:body response))]
        (:status response) => 200))

    (fact "Incorrect credentials"
      (let [response (app (-> (mock/request :post "/api/v0/session")
                              (mock/content-type "application/json")
                              (mock/body (cheshire/generate-string {:username "team_1_player_1"
                                                                    :password "password123"}))))]
        (:status response) => 401))))