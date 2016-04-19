(ns pigeon-backend.routes.hello-test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(def test-token "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJ1c2VyIjoiZm9vYmFyIiwicm9sZXMiOlsiYXBwLWZyb250cGFnZSJdfQ.pAxX-x7zT_deUOpqi2hCmZySYMtwa-yGlocDhH_alKc")

(deftest hello-test
  (facts "Testing /hello"
    (fact "Test GET request to /hello?name={a-name} returns expected response"
      (let [response (app (-> (assoc-in (mock/request :get "/hello?name=Stranger")
                                        [:cookies "token" :value]
                                        test-token)))
            body     (parse-body (:body response))]
        (:status response) => 200
        (:message body)    => "Terve, Stranger"))
    (fact "Test GET request to /hello/en?name={a-name} returns expected response"
      (let [response (app (-> (assoc-in (mock/request :get "/hello/en?name=Stranger")
                                        [:cookies "token" :value]
                                        test-token)))
            body     (parse-body (:body response))]
        (:status response) => 200
        (:message body)    => "Hello, Stranger"))))