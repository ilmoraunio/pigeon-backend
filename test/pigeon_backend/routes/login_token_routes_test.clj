(ns pigeon-backend.routes.login-token-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [midje.sweet :refer :all]
            [schema.core :as s]))

(deftest login
  (facts "Token service"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Must send magic link after valid email")
      (fact "Fails gracefully after invalid email")
      (fact "User must exist before magic link can be sent"))))