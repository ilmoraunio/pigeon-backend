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
            [environ.core :refer [env]]
            [clojure.java.jdbc :as jdbc]
            [ring.mock.request :as mock]
            [pigeon-backend.handler :refer [app]]))

(defn empty-and-create-tables []
  (empty-all-tables db-spec)
  (if (= 0 (count (get-table-names db-spec)))
    (migrations/migrate)))

(defmacro without-fk-constraints [tx & body]
  `(do
    (disable-fks-in-postgres ~tx)
    (let [result# (do ~@body)]
      (enable-fks-in-postgres ~tx)
      result#)))

(defn disable-fks-in-postgres [tx]
  (jdbc/execute! tx ["SET session_replication_role = replica"]))

(defn enable-fks-in-postgres [tx]
  (jdbc/execute! tx ["SET session_replication_role = DEFAULT"]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(def test-token "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJ1c2VyIjoiZm9vYmFyIn0.gam31MTKYrmqZ4OlHcBUPALjMFUcQ48KIGDzRUBxBc0")

(defn create-login-token [username timestamp jws-shared-secret]
  (jws/sign {:user username
             :expires timestamp}
            jws-shared-secret))

(def test-user "username")

(defn create-test-login-token
  ([] (create-test-login-token test-user))
  ([user] (create-login-token user
            (str (t/plus (t/now) (t/hours 4)))
            (env :jws-shared-secret))))

(def clj-timestamp #"[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\.[0-9]{1,3}Z")

;; route requests: new

(defn new-account
  ([input] (app (-> (mock/request :put "/api/v0/user")
                    (mock/content-type "application/json")
                    (mock/body (cheshire/generate-string input)))))
  ([] (new-account {:username test-user
                    :password "password"
                    :name "name"})))