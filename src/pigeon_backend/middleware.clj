(ns pigeon-backend.middleware
  (require [buddy.sign.jws :as jws]
           [pigeon-backend.services.exception-util :refer [handle-exception-info]]
           [environ.core :refer [env]]))

(defn wrap-authentication [handler]
  (fn [request]

    (prn "uri" (:uri request))
    (prn "params" (:query-params request))
    (prn (contains? (:query-params request) "api_key"))

    (if-not (contains? (:query-params request) "api_key")
      (handle-exception-info
          (ex-info "Not logged in" {:type :validation
                                    :cause :signature}) {} request))

    (let [api-key {:key "api_key"
                   :value (get (:query-params request) "api_key")}
          token (:value api-key)]
      (if (or (empty? token) (< (count token) 3))
        (handle-exception-info
          (ex-info "Not logged in" {:type :validation
                                    :cause :signature}) {} request)
        (do
          (jws/unsign token (env :jws-shared-secret))
          (handler request))))))