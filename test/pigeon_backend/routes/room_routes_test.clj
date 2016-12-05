(ns pigeon-backend.routes.room-routes-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [pigeon-backend.handler :refer [app]]
            [ring.mock.request :as mock]
            [pigeon-backend.test-util :refer :all]
            [midje.sweet :refer :all]
            [schema.core :as s]))

(defn new-room
  ([input] (app (-> (mock/request :post "/api/v0/room")
                    (mock/content-type "application/json")
                    (mock/header "Authorization" (str "Bearer " (create-test-login-token)))
                    (mock/body (cheshire/generate-string input)))))
  ([] (new-room {:name "Room!"})))

(deftest room-routes-test
  (facts
    (with-state-changes [(before :facts (empty-and-create-tables))]

      (fact "Create a room"
        (let [room     {:name "Room!"}
              response (new-room room)
              body     (parse-body (:body response))]
          (:status response) => 200
          body => (contains room
                            {:id integer?})))

      (fact "Get rooms (no rooms created)"
        (let [search-criteria nil
              response        (app (-> (mock/request :get "/api/v0/room")
                                       ;; TODO: shouldn't all search criteria be in the URL...?
                                       (mock/content-type "application/json")
                                       (mock/header "Authorization" (str "Bearer " (create-test-login-token)))
                                       (mock/body (cheshire/generate-string search-criteria))))
              body            (parse-body (:body response))]
          (:status response) => 200
          body => (contains [])))

      (fact "Get rooms (one room created)"
        (let [room            {:name "Room!"}
              _               (new-room room)
              search-criteria nil
              response        (app (-> (mock/request :get "/api/v0/room")
                                       (mock/content-type "application/json")
                                       (mock/header "Authorization" (str "Bearer " (create-test-login-token)))
                                       (mock/body (cheshire/generate-string search-criteria))))
              body            (parse-body (:body response))]
          (:status response) => 200
          body => (one-of coll?)
          body => (contains [(contains room
                                       {:id integer?})])))

      (fact "Get rooms (many rooms created)"
        (let [room1           {:name "Room1!"}
              room2           {:name "Room2!"}
              _               (new-room room1)
              _               (new-room room2)
              search-criteria nil
              response        (app (-> (mock/request :get "/api/v0/room")
                                       (mock/content-type "application/json")
                                       (mock/header "Authorization" (str "Bearer " (create-test-login-token)))
                                       (mock/body (cheshire/generate-string search-criteria))))
              body            (parse-body (:body response))]
          (:status response) => 200
          body => (two-of coll?)
          body => (contains [(contains room1)
                             (contains room2)]))))))