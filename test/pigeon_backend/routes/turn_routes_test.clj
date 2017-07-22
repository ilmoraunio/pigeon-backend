(ns pigeon-backend.routes.turn-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [midje.sweet :refer :all]
            [schema.core :as s]
            [pigeon-backend.db.migrations :as migrations]))


(deftest turn-routes-test
         (with-state-changes [(before :facts (do (empty-and-create-tables)
                                                 ;; todo: fix tests to be deterministic
                                                 (migrations/migrate-data-extra)))]
           (fact "Listing"
             (let [response (app (-> (mock/request :get "/api/v0/turn")
                                     (mock/content-type "application/json")
                                     (mock/header :authorization (tokenize token-team-1-player-1))))
                   body     (parse-body (:body response))]
               (:status response) => 200
               body => #(n-of % 22)))

           ;; todo: w/ moderator powers only
           (fact "Activate turn"
             (let [response (app (-> (mock/request :post "/api/v0/turn/2")
                                     (mock/content-type "application/json")
                                     (mock/header :authorization (tokenize token-moderator))))
                   body     (parse-body (:body response))]
               (:status response) => 200
               body => (contains {:id 2})))))