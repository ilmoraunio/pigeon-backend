(ns pigeon-backend.test-util
  (:require [pigeon-backend.migrations_test :refer [empty-all-tables
                                                    drop-all-tables]]
            [pigeon-backend.db.config :refer [db-spec 
                                              get-table-names
                                              get-migrations]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.db.migrations :as migrations]
            [cheshire.core :as cheshire]
            [buddy.sign.jws :as jws]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(defn empty-and-create-tables []
  (empty-all-tables db-spec)
  (if (= 0 (count (get-table-names)))
    (migrations/migrate)))

(defn drop-and-create-tables []
  (drop-all-tables db-spec)
  (migrations/migrate))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(def test-token "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJ1c2VyIjoiZm9vYmFyIn0.gam31MTKYrmqZ4OlHcBUPALjMFUcQ48KIGDzRUBxBc0")

(defn login-as-test-user [mock-request]
  (assoc-in mock-request [:cookies "token" :value] test-token))

(def clj-timestamp #"[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\.[0-9]{1,3}Z")

(defn create-login-token [username timestamp jws-shared-secret]
  (jws/sign {:user username
             :expires timestamp}
            jws-shared-secret))

(defn create-test-login-token []
  (create-login-token "foobar"
                      (str (t/plus (t/now) (t/hours 4)))
                      (env :jws-shared-secret)))