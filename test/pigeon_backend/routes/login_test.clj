(ns pigeon-backend.routes.login-test
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
                                              clj-timestamp]]
            [pigeon-backend.services.user-service :as user-service]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(def user-dto {:username "foobar" 
               :password "hunter2"})

(def user-dto-with-wrong-password {:username "foobar" 
                                   :password "password123"})

(def registration-dto {:username "foobar" 
                       :password "hunter2"
                       :full_name "Mr Foo Bar"})

(def test-token "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJ1c2VyIjoiZm9vYmFyIn0.gam31MTKYrmqZ4OlHcBUPALjMFUcQ48KIGDzRUBxBc0")

(deftest login-test
  (facts "Route: login & logout"
    (with-state-changes [(before :facts (empty-and-create-tables))]

      (fact "Login: success"
        (user-service/user-create! registration-dto)
        (let [{status :status body :body} 
                ((app-with-middleware)
                 (mock/content-type
                  (mock/body
                    (mock/request :post "/api/v0/session")
                    (json/write-str user-dto))
                  "application/json"))]
          status => 200
          (parse-body body) => {:session {:token test-token}}))
      (fact "Login: unsuccess"
        (user-service/user-create! registration-dto)
        (let [{status :status body :body} 
                ((app-with-middleware)
                 (mock/content-type
                  (mock/body
                    (mock/request :post "/api/v0/session")
                    (json/write-str user-dto-with-wrong-password))
                  "application/json"))]
          status => 401
          body => nil))
      (fact "Login/Logout responses"
        (user-service/user-create! registration-dto)
        (let [login-response ((app-with-middleware)
                               (mock/content-type
                                (mock/body
                                  (mock/request :post "/api/v0/session")
                                  (json/write-str user-dto))
                                "application/json"))
              logout-response ((app-with-middleware)
                               (mock/content-type
                                (mock/body
                                  (mock/request :delete "/api/v0/session")
                                  (json/write-str user-dto))
                                "application/json"))]
          (first (get-in login-response [:headers "Set-Cookie"])) 
            => (str "token=" test-token ";"
                    "Max-Age=14400;"
                    "HttpOnly;Path=/")
          (first (get-in logout-response [:headers "Set-Cookie"]))
            => (str "token=nil;Path=/;Expires=Thu, 01 Jan 1970 00:00:00 GMT")))))

  (facts "Route: authenticated"
    (with-state-changes [(before :facts (empty-and-create-tables))]
      (fact "Is not authenticated"
        (user-service/user-create! registration-dto)
        (let [{status :status
               body :body} 
                ((app-with-middleware)
                  (mock/request :get "/api/v0/hello?name=foo"))]
          status => 401
          (parse-body body) => {:title "Not logged in"
                                :cause "signature"
                                :error-status 401}))

      (fact "Tamper token value as incorrect"
        (user-service/user-create! registration-dto)
        (let [{status :status
               body :body}
                ((app-with-middleware)
                  ;; logged in
                  (assoc-in (mock/request :get (str "/api/v0/hello?name=foo" 
                                                    "&api_key=" 
                                                    (create-login-token "foobar"
                                                      (str (t/plus (t/now) (t/hours 4)))
                                                      (env :jws-shared-secret))
                                                    "CORRUPT_OR_HACKED"))
                            [:cookies "token" :value]
                            (str test-token "CORRUPT_OR_HACKED")))]
          status => 401
          (parse-body body) => {:title "Message seems corrupt or manipulated."
                                :cause "signature"
                                :error-status 401})))))