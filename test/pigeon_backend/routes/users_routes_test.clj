(ns pigeon-backend.routes.login-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [midje.sweet :refer :all]
            [schema.core :as s]))

(deftest users-routes-test
  (with-state-changes [(before :facts (empty-and-create-tables))]
    (fact
      (let [;; todo: decomment once users data initialization ddl removed
            account1 {:username "foo"
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
        body => (contains [(contains {:username "bar"})])))))