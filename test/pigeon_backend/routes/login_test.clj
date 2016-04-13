(ns pigeon-backend.routes.login-test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [pigeon-backend.dao.user-dao :refer [sql-user-get-all]]
            [pigeon-backend.test-util :refer [drop-and-create-tables
                                              parse-body]]))

(def user-dto {:username "foobar" 
               :password "hunter2"})

(deftest login-test
  (facts "Route: login"
    (with-state-changes [(before :facts (drop-and-create-tables))]

      (fact "Success"
        (let [{status :status body :body} 
                ((app-with-middleware)
                 (mock/content-type
                  (mock/body
                    (mock/request :post "/user/login")
                    (json/write-str user-dto))
                  "application/json"))]
          status => 200
          body => nil)))))