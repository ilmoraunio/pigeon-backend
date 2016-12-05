(ns pigeon-backend.routes.login-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [midje.sweet :refer :all]
            [schema.core :as s]
            [pigeon-backend.routes.registration-routes-test :refer [new-account]]))

(deftest login-routes-test
  (facts
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact
        (let [account  {:username "User!"
                        :password "hunter2"
                        :full_name "Full Name!"}
              _        (new-account account)
              response (app (-> (mock/request :post "/api/v0/session")
                                (mock/content-type "application/json")
                                (mock/body (cheshire/generate-string (dissoc account :full_name)))))
              body     (parse-body (:body response))]
          (:status response) => 200)))))