(ns pigeon-backend.middleware
  (require [buddy.sign.jws :as jws]
           [pigeon-backend.services.exception-util :refer [handle-exception-info]]
           [environ.core :refer [env]]))

(defn wrap-auth [handler]
  (fn [request]
    (let [headers (:headers request)]

      (if-not (contains? headers "authorization")
        (handle-exception-info
          (ex-info "Not logged in" {:type :validation
                                    :cause :signature}) {} request))

      (if (nil? (get headers "authorization"))
        (handle-exception-info
          (ex-info "Not logged in" {:type :validation
                                    :cause :signature}) {} request)
        (let [token (second (re-find #"Bearer (.*?)$" (get headers "authorization")))]
          (if (or (empty? token) (< (count token) 3))
            (handle-exception-info
              (ex-info "Not logged in" {:type :validation
                                        :cause :signature}) {} request)
            (do
              (jws/unsign token (env :jws-shared-secret))
              (handler request))))))))