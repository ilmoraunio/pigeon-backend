(ns pigeon-backend.util)

(defn parse-auth-key [request]
  (let [headers (:headers request)]
    (second (re-find #"Bearer (.*?)$" (get headers "authorization")))))