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

(def test-token "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJ1c2VyIjoiZm9vYmFyIiwicm9sZXMiOlsiYXBwLWZyb250cGFnZSJdfQ.pAxX-x7zT_deUOpqi2hCmZySYMtwa-yGlocDhH_alKc")

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
          body => nil))))

  (facts "Route: authenticated"
    (with-state-changes [(before :facts (drop-and-create-tables))]
      (fact "Is not authenticated"
        (user-service/user-create! registration-dto)
        (let [{status :status
               body :body} 
                ((app-with-middleware)
                  (mock/request :get "/user/authenticated"))]
          status => 401
          (parse-body body) => {:title "Not logged in"
                                :cause "signature"
                                :error-status 401}))

      (fact "Is authenticated"
        (user-service/user-create! registration-dto)
        (let [{status :status
               body :body}
                ((app-with-middleware)
                  ;; logged in
                  (assoc-in (mock/request :get "/user/authenticated")
                            [:cookies "token" :value]
                            test-token))]
          status => 200
          (parse-body body) => {:token-unsigned {:user "foobar"
                                                 :roles ["app-frontpage"]}}))

      (fact "Tamper token value as incorrect"
        (user-service/user-create! registration-dto)
        (let [{status :status
               body :body}
                ((app-with-middleware)
                  ;; logged in
                  (assoc-in (mock/request :get "/user/authenticated")
                            [:cookies "token" :value]
                            (str test-token "DENIED")))]
          status => 401
          (parse-body body) => {:title "Message seems corrupt or manipulated."
                                :cause "signature"
                                :error-status 401})))))