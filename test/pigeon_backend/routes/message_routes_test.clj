(ns pigeon-backend.routes.message-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [midje.sweet :refer :all]
            [schema.core :as s]))

(deftest message-routes-test
  (with-state-changes [(before :facts (empty-and-create-tables))]
    (fact "Insertion"
      (let [account1  {:username "foo"
                       :password "hunter2"
                       :name "name"}
            account2  {:username "bar"
                       :password "hunter2"
                       :name "name"}
            _        (new-account account1)
            _        (new-account account2)
            response (app (-> (mock/request :post "/api/v0/sender/foo/recipient/bar")
                              (mock/content-type "application/json")
                              (mock/body (cheshire/generate-string {:message "message"}))))
            body     (parse-body (:body response))]
        (:status response) => 200
        (:message body) => "message"))

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
            response (app (-> (mock/request :get "/api/v0/sender/foo/recipient/bar")
                              (mock/content-type "application/json")))
            body     (parse-body (:body response))]
        (:status response) => 200
        body => two-of))))