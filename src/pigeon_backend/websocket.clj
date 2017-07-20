(ns pigeon-backend.websocket
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [ring.server.standalone :as ring]
            [environ.core :refer [env]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [buddy.sign.jws :as jws]
            [immutant.web :as immutant]
            [immutant.web.async :as async]
            [immutant.web.middleware :refer [wrap-websocket]]
            [cognitect.transit :as transit])
  (:import  [java.io
             ByteArrayOutputStream]))

(defonce channels (atom {}))

(defn async-send! [users-to-channels message]
  (let [out (ByteArrayOutputStream. 4096)
        writer (transit/writer out :json)
        _ (transit/write writer message)
        message (.toString out)]
    (prn "[Websocket OUT]" (keys users-to-channels) message)
    (doseq [[_ channels] users-to-channels]
      (doseq [channel channels]
        (async/send! channel message)))))

(defn ws-app
  "For passing information when to reload messages,
                                   reload turns,
                                   or to get notifications of new messages"
  [request username]
  (async/as-channel request
    {:on-open    (fn [channel]
                   (swap! channels
                     update username #(into #{} (conj %1 channel)))
                   (async-send! {username #{channel}} "Async ready")
                   (prn "channel open" @channels))
     :on-message (fn [channel m]
                   ;; todo: enable support
                   ;; (and don't allow broadcasting, unless moderator-restricted)
                   ;; (doseq [channel @channels]
                   ;;   (async/send! channel m))
                   )
     :on-close   (fn [channel {:keys [code reason]}]
                   (prn "close code:" code "reason:" reason)
                   (swap! channels update username #(disj %1 channel))
                   (swap! channels #(into {}
                                      (filter
                                        (fn [[_ channels]] (not-empty channels))
                                        %1))))}))