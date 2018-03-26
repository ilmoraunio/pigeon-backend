(ns pigeon-backend.services.exception-util
  (:require [schema.core :as s]
            [ring.util.http-response :refer :all]))

(defn- status-code-for [type cause]
  (cond
    (= type :username-exists)  400
    (and (= type :validation)
         (= cause :signature)) 401
    :else                      400))

(defn- return-exception-response 
  [exception-args]
  {:pre [(s/validate {:error-status s/Int
                      :title String
                      :cause s/Any} exception-args)]}
  (let [{error-status :error-status} exception-args]
    (cond
      (= error-status 401) (unauthorized exception-args)
      :else (bad-request exception-args))))

(defn- return-exception-message [e]
  (let [type (:type (ex-data e))
        cause (:cause (ex-data e))
        status-code (status-code-for type cause)]
    (return-exception-response {:error-status status-code
                                :title (.getMessage e)
                                :cause cause})))

;; public fns

(s/defschema ErrorMessage
  {:error-status s/Int
   :title String
   :cause String})

(defn handle-exception-info [^Exception e data request]
  (prn e)
  (return-exception-message e))