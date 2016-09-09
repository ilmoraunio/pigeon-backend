(ns pigeon-backend.routes.hello-test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer [login-as-test-user]]
            [pigeon-backend.test-util :refer [create-test-login-token
                                              clj-timestamp]]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(deftest hello-test
  (facts "Testing /hello"
    (fact "Test GET request to /hello?name={a-name} returns expected response"

      (let [response (app (-> (login-as-test-user (mock/request :get "/api/v0/hello?name=Stranger"))))
            body     (parse-body (:body response))]
        (:status response) => 200
        (:message body)    => "Terve, Stranger"))
    (fact "Test GET request to /hello/en?name={a-name} returns expected response"
      (let [response (app (-> (login-as-test-user (mock/request :get "/api/v0/hello/en?name=Stranger"))))
            body     (parse-body (:body response))]
        (:status response) => 200
        (:message body)    => "Hello, Stranger"))))