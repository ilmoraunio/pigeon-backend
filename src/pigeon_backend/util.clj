(ns pigeon-backend.util
  (:require [schema.core :as s]))

(defn parse-auth-key [request]
  (let [headers (:headers request)]
    (second (re-find #"Bearer (.*?)$" (get headers "authorization")))))

(def AuthorizationKey String)