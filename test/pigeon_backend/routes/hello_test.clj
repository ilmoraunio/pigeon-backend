(ns pigeon-backend.routes.hello-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [pigeon-backend.dao.user-dao :refer [sql-user-get-all]]
            [pigeon-backend.test-util :refer [drop-and-create-tables
                                              parse-body]]))

(deftest hello-test
  (facts "Route: hello"
    (with-state-changes [(before :facts (drop-and-create-tables))]
      (fact "Unauthorized test (WIP)"
        (let [{status :status body :body} 
                ((app-with-middleware)
                  (mock/request :get "/hello?name=foo"))]
          status => 401
          (parse-body body) => {:cause "signature"
                                :error-status 401
                                :title "Not logged in"})))))