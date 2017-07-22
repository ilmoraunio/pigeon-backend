(ns pigeon-backend.middleware
  (require [buddy.sign.jws :as jws]
           [pigeon-backend.services.exception-util :refer [handle-exception-info]]
           [environ.core :refer [env]]
           [pigeon-backend.util :refer :all]))

(def ^{:private true} authorizable-http-methods #{:get :head :post :put :delete :patch})

(defn wrap-auth [handler]
  (fn [request]
    (let [headers (:headers request)]
      (if-let [should-request-be-authorized? ((:request-method request) authorizable-http-methods)]
        (do
          (if-not (contains? headers "authorization")
            (handle-exception-info
              (ex-info "Not logged in" {:type :validation
                                        :cause :signature}) {} request))
          (if (nil? (get headers "authorization"))
            (handle-exception-info
              (ex-info "Not logged in" {:type :validation
                                        :cause :signature}) {} request)
            (let [token (parse-auth-key request)]
              (if (or (empty? token) (< (count token) 3))
                (handle-exception-info
                  (ex-info "Not logged in" {:type :validation
                                            :cause :signature}) {} request)
                (do
                  (jws/unsign token (env :jws-shared-secret))
                  (handler request))))))

        (handler request)))))

(defn wrap-authorize
  "Used after wrap-auth"
  [kws handler]
  (fn [request]
    (if-let [username (get-in request kws)]
      (if-let [is-a-match?
               (= username
                 (:user (jws/unsign
                          (parse-auth-key request)
                          (env :jws-shared-secret))))]
        (handler request)
        (handle-exception-info
          (ex-info "Not authorized" {:type :validation
                                     :cause :authorization}) {} request))
      (handle-exception-info
        (ex-info "Missing argument" {:type  :validation
                                     :cause :authorization}) {} request))))