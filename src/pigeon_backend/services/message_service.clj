(ns pigeon-backend.services.message-service
  (:require [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [schema.core :as s]
            [pigeon-backend.services.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.services.util :refer [initialize-query-data]]
            [jeesql.core :refer [defqueries]]))

(s/defschema New {:sender String
                  :recipient String
                  :message String})
(s/defschema Model (merge model/Model New {:actual_recipient String
                                           :turn s/Int}))
(s/defschema Get {:sender String
                  :recipient String
                  :turn s/Int})

(defqueries "sql/message.sql")
(defqueries "sql/send_limit.sql")
(defqueries "sql/turn.sql")

(s/defn message-create! [{:keys [sender recipient] :as data} :- New] {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (let [active-turn (->> (sql-turn-get tx)
                           (filter #(true? (:active %)))
                           first)
          messages-for-active-turn (->> (sql-message-get tx {:sender sender
                                                             :recipient recipient
                                                             :turn (:id active-turn)})
                                        (filter #(= (:sender %) sender)))
          message-quota (->> (sql-get-send-limit tx)
                             (filter #(and (= (:from_node %) sender)
                                           (some #{recipient} (:to_nodes %))))
                             (map :value)
                             (reduce max 0)) ;; strict by default
          ]
      (when (>= (count messages-for-active-turn) message-quota)
        (throw (ex-info "Message quota exceeded" data)))
      (sql-message-create<! tx (assoc data :actual_recipient recipient)))))

(s/defn message-get [data :- Get] {:post [(s/validate [(assoc Model :is_from_sender Boolean
                                                                    :turn_name String
                                                                    :sender_name String)] %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (sql-message-get tx data)))