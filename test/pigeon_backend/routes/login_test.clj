(ns pigeon-backend.routes.login-test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [pigeon-backend.dao.user-dao :refer [sql-user-get-all]]
            [pigeon-backend.test-util :refer [drop-and-create-tables
                                              parse-body]]
            [pigeon-backend.services.user-service :as user-service]
            [buddy.sign.jws :as jws]))

(def user-dto {:username "foobar" 
               :password "hunter2"})

(def user-dto-with-wrong-password {:username "foobar" 
                                   :password "password123"})

(def registration-dto {:username "foobar" 
                       :password "hunter2"
                       :full_name "Mr Foo Bar"})

(def test-token "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJ1c2VyIjoiZm9vYmFyIn0.gam31MTKYrmqZ4OlHcBUPALjMFUcQ48KIGDzRUBxBc0")

(deftest login-test
  (facts "Route: login"
    (with-state-changes [(before :facts (drop-and-create-tables))]

      (fact "Success"
        (user-service/user-create! registration-dto)
        (let [{status :status body :body} 
                ((app-with-middleware)
                 (mock/content-type
                  (mock/body
                    (mock/request :post "/user/login")
                    (json/write-str user-dto))
                  "application/json"))]
          status => 200
          (parse-body body) => {:token test-token}))
      (fact "Unsuccess"
        (user-service/user-create! registration-dto)
        (let [{status :status body :body} 
                ((app-with-middleware)
                 (mock/content-type
                  (mock/body
                    (mock/request :post "/user/login")
                    (json/write-str user-dto-with-wrong-password))
                  "application/json"))]
          status => 401
          body => nil)))))