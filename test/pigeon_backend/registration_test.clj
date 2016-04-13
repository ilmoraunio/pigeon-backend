(ns pigeon-backend.registration-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [pigeon-backend.dao.user-dao :refer [sql-user-get-all]]
            [pigeon-backend.test-util :refer [drop-and-create-tables
                                              parse-body]]))

(def user-dto {:username "foobar" 
               :password "hunter2" 
               :full_name "Mr Foo Bar"})

(deftest registration-test
  (facts "Route: registration"
    (with-state-changes [(before :facts (drop-and-create-tables))]

      (fact "Basic case"
        (let [{status :status body :body} 
                ((app-with-middleware)
                 (mock/content-type
                  (mock/body
                    (mock/request :put "/user")
                    (json/write-str user-dto))
                  "application/json"))]
          status => 201
          body => nil))

      (fact "Duplicate username"
        (let [_ ((app-with-middleware)
                  (mock/content-type
                    (mock/body
                      (mock/request :put "/user")
                      (json/write-str user-dto))
                    "application/json"))
              {status :status body :body}
                ((app-with-middleware)
                 (mock/content-type
                   (mock/body
                     (mock/request :put "/user")
                     (json/write-str user-dto))
                   "application/json"))]
          status => 400
          (parse-body body) => {:error-status 400,
                                :title "Duplicate username"
                                :detail "User foobar already exists"})))))