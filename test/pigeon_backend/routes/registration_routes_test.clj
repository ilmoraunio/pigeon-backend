(ns pigeon-backend.routes.registration-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [midje.sweet :refer :all]
            [schema.core :as s]))

(defn new-account
  ([input] (app (-> (mock/request :put "/api/v0/user")
                    (mock/content-type "application/json")
                    (mock/body (cheshire/generate-string input)))))
  ([] (new-account {:username "Username!"
                    :password "hunter2"
                    :full_name "Real name!"})))

(deftest registration-routes-test
  (facts
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Register an account"
        (let [response (new-account)]
          (:status response) => 201)))))