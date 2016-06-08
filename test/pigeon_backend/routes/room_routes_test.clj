(ns pigeon-backend.routes.room-routes-test
  (:require [clojure.test :refer [deftest]]
            [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [pigeon-backend.test-util :refer [empty-and-create-tables
                                              parse-body]]
            [pigeon-backend.services.room-service :as room-service]
            [buddy.sign.jws :as jws]))

(def room-data {:name "Huone"})

(deftest room-routes-test
  (facts "Route: room"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Create"
        (let [{status :status body :body}
              ((app-with-middleware)
               (mock/content-type
                (mock/body
                  (mock/request :post "/room")
                  (json/write-str room-data))
                "application/json"))]
          status => 200
          (parse-body body) => (contains {:name "Huone"})))
      (fact "Update"
        (let [{id :id} (room-service/room-create! room-data)
              {status :status body :body}
              ((app-with-middleware)
               (mock/content-type
                (mock/body
                  (mock/request :put "/room")
                  (json/write-str {:id id
                                   :name "Huone 2"}))
                "application/json"))]
          status => 200
          (parse-body body) => (contains {:name "Huone 2"}))))))