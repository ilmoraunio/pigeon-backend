(ns pigeon-backend.registration-test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [pigeon-backend.dao.user-dao :refer [sql-user-get-all]]
            [pigeon-backend.test-util :refer [drop-and-create-tables]]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(deftest registration-test
  (facts "Registration"
    (with-state-changes [(before :facts (drop-and-create-tables))]
      (fact "Basic case"
        (let [{status :status body :body} 
                (app
                 (mock/content-type
                  (mock/body
                    (mock/request :put "/user")
                    (json/write-str {:username "foobar" 
                                     :password "hunter2" 
                                     :full_name "Mr Foo Bar"}))
                  "application/json"))]
          status => 201
          body => nil)
          (-> (sql-user-get-all) count) => 1))))