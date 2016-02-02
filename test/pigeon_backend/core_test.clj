(ns pigeon-backend.core-test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(deftest core-test
  (facts "Testing /hello"
    (fact "Test GET request to /hello?name={a-name} returns expected response"
      (let [response (app (-> (mock/request :get  "/hello?name=Stranger")))
            body     (parse-body (:body response))]
        (:status response) => 200
        (:message body)    => "Terve, Stranger"))
    (fact "Test GET request to /hello/en?name={a-name} returns expected response"
      (let [response (app (-> (mock/request :get "/hello/en?name=Stranger")))
            body     (parse-body (:body response))]
        (:status response) => 200
        (:message body)    => "Hello, Stranger"))))