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
                  :recipient String})

(defqueries "sql/message.sql")
(defqueries "sql/send_limit.sql")
(defqueries "sql/turn.sql")

(s/defn message-create! [{:keys [sender recipient] :as data} :- New] {:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (let [active-turn (->> (sql-turn-get tx)
                           (filter #(true? (:active %)))
                           first)
          ;; todo: some duplicate code below...
          send-limit-rules (->> (sql-get-send-limit tx)
                                (filter #(and (= (:from_node %) sender)
                                          (some #{recipient} (:to_nodes %))
                                          (= (:type %) "send_limit"))))
          send-limit-rules (doall (map (fn [{:keys [from_node to_nodes] :as value}]
                                         (assoc value :messages
                                                      (sql-conversations tx
                                                        {:sender from_node
                                                         :recipient to_nodes
                                                         :turn (:id active-turn)})))
                                       send-limit-rules))
          all-rule-limits-exceeded? (every? true?
                                            (for [entry send-limit-rules]
                                              (>= (count (:messages entry)) (:value entry))))
          shared-send-limit-rules (->> (sql-get-send-limit tx)
                                       (filter #(and (= (:from_node %) sender)
                                                 (some #{recipient} (:to_nodes %))
                                                 (= (:type %) "shared_send_limit"))))
          shared-send-limit-rules (doall (map (fn [{:keys [from_node to_nodes] :as value}]
                                                (assoc value :messages
                                                             (sql-conversations tx
                                                               {:sender from_node
                                                                :recipient to_nodes
                                                                :turn (:id active-turn)})))
                                              shared-send-limit-rules))
          all-shared-rule-limits-exceeded? (every? true?
                                                   (for [entry shared-send-limit-rules]
                                                     (>= (count (:messages entry)) (:value entry))))]
      (when (and all-rule-limits-exceeded?
                 all-shared-rule-limits-exceeded?)
        (throw (ex-info "Message quota exceeded" data)))

      (sql-message-create<! tx (assoc data :actual_recipient recipient)))))

(s/defn message-get [data :- Get] {:post [(s/validate [(assoc Model :is_from_sender Boolean
                                                                    :turn_name String
                                                                    :sender_name String)] %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (sql-message-get tx data)))