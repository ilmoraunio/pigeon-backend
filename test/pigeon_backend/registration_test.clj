(ns pigeon-backend.registration-test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(deftest registration-test
  (facts "Registration"
    (fact "Basic case"
      (let [{status :status body :body} 
              (app
               (mock/content-type
                (mock/body
                  (mock/request :put "/user")
                  (json/write-str {:username "foobar" :password "hunter2"}))
                "application/json"))]
        status => 201
        body    => nil))))