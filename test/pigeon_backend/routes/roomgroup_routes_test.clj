(ns pigeon-backend.routes.roomgroup-routes-test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [pigeon-backend.dao.user-dao :refer [sql-user-get-all]]
            [pigeon-backend.test-util :refer [empty-and-create-tables
                                              parse-body
                                              create-login-token
                                              create-test-login-token
                                              clj-timestamp]]
            [pigeon-backend.services.user-service :as user-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(deftest roomgroup-test
  (facts "User should be able to add himself to room")
  (facts "User should be able to remove himself from room")
  (facts "User should be able to see all existing aliases in room"))