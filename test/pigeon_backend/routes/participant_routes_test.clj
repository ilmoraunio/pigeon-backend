(ns pigeon-backend.routes.participant-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [midje.sweet :refer :all]
            [schema.core :as s]
            [pigeon-backend.routes.room-routes-test :refer [new-room]]))

(deftest participant-routes-test
  (facts
    (with-state-changes [(before :facts (empty-and-create-tables))]
      ;; TODO
      (comment (fact "Join a room")))))