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
            [cognitect.transit :as transit]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.immutant :refer [get-sch-adapter]])
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

(defmulti -event-msg-handler
  "Sente dispatcher"
  :id ;; dispatch based on event id
  )

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event ?reply-fn]}]
  (-event-msg-handler ev-msg) ;; handle events on a single thread
  ;; (future (-event-msg-handler ev-msg)) ;; Handle event-msgs on a thread pool
  )

(defmethod -event-msg-handler
  :default
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (when ?reply-fn
    (?reply-fn {:unmatched-event-as-echoed-from-server event})))

(let [{:keys [ch-recv
              send-fn
              connected-uids
              ajax-post-fn
              ajax-get-or-ws-handshake-fn]}
      (sente/make-channel-socket! (get-sch-adapter) {})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom

  (sente/start-server-chsk-router! ch-chsk event-msg-handler))