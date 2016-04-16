(ns pigeon-backend.middleware
  (require [buddy.sign.jws :as jws]
           [pigeon-backend.services.exception-util :refer [handle-exception-info]]
           [environ.core :refer [env]]))

(defn wrap-authentication [handler]
  (fn [request]
    (let [{{{token-value :value} "token"} :cookies} request]
      (if (or (empty? token-value) (< (count token-value) 3))
        (do
          (handle-exception-info
                  (ex-info "Not logged in" {:type :validation
                                            :cause :signature}) {} request))
        (do
          (jws/unsign token-value (env :jws-shared-secret))
          (handler request))))))