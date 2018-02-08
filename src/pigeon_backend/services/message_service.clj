(ns pigeon-backend.services.message-service
  (:require [clojure.java.jdbc :as jdbc]
            [pigeon-backend.db.config :refer [db-spec]]
            [schema.core :as s]
            [pigeon-backend.services.model :as model]
            [schema-tools.core :as st]
            [pigeon-backend.services.util :refer [initialize-query-data]]
            [jeesql.core :refer [defqueries]]
            [pigeon-backend.websocket :refer [channels async-send!]]
            [environ.core :refer [env]]
            [instaparse.core :as insta])
  (:import (clojure.lang Keyword)))

(s/defschema New {:sender String
                  :recipient String
                  :message String})

(s/defschema MessagePayload {:message String
                             :sender String
                             :recipient String
                             :message_attempt s/Int
                             :actual_recipient String})

(s/defschema Model (merge model/Model New {:message_attempt s/Int
                                           :turn s/Int}))
(s/defschema Get {:sender String
                  :recipient String})

(s/defschema Rule {:ref String
                   :value s/Any
                   :realized_value s/Any
                   :from_node (s/maybe String)
                   :to_node String
                   :type String ;; todo: as enum
                   :short_circuit_rule_chain_if_true Boolean
                   :short_circuit_rule_chain_if_false Boolean
                   :order_no s/Int
                   :if_satisfied_then_direct_to_nodes [String]
                   :if_satisfied_then_duplicate_to_nodes [String]
                   :if_satisfied_then_duplicate_from_nodes [String]})

(s/defschema Deletable {:id s/Int})
(def Undeletable Deletable)

(defqueries "sql/message.sql")
(defqueries "sql/send_limit.sql")
(defqueries "sql/turn.sql")
(defqueries "sql/rule.sql")

(def rules (atom (read-string (slurp "rules.edn"))))
(def ^:dynamic *params*)

(s/defn send-message [tx {:keys [sender recipient] :as message-payload} :- MessagePayload]
  (sql-message-create<! tx message-payload)

  (async-send!
    (filter (fn [[k _]] (= k recipient)) @channels)
    [:message-received sender])

  (async-send!
    (filter (fn [[k _]] (or (= k sender)
                          (= k recipient))) @channels)
    [:reload-messages]))

(defmulti randomize-value class)
(defmethod randomize-value Long [n]
  (rand-nth (range n)))
(defmethod randomize-value Double [n]
  (rand n))

(defn- local-eval [x]
  (binding [*ns* (find-ns 'pigeon-backend.services.message-service)]
    (eval x)))

(s/defn message-create! [{:keys [sender recipient message] :as data} :- New]
  ;;{:post [(s/validate Model %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (let [active-turn (->> (sql-turn-get tx)
                           (filter #(true? (:active %)))
                           first)
          send-limits (sql-get-send-limit tx)
          ;; todo: some duplicate code below...
          send-limit-rules (doall (map (fn [{:keys [from_node to_nodes] :as value}]
                                         (assoc value :messages
                                                      (sql-conversations tx
                                                        {:sender from_node
                                                         :recipient to_nodes
                                                         :turn (:id active-turn)})))
                                       (->> send-limits
                                            (filter #(and (= (:from_node %) sender)
                                                          (some #{recipient} (:to_nodes %))
                                                          (= (:type %) "send_limit"))))))
          all-rule-limits-exceeded? (every? true?
                                            (for [entry send-limit-rules
                                                  :let [messages-counted-by-recipient
                                                          (into {}
                                                            (mapv
                                                              (fn [[k v]] {k (count v)})
                                                              (group-by :recipient (:messages entry))))]]
                                              (>= (get messages-counted-by-recipient recipient 0) (:value entry))))
          shared-send-limit-rules (doall (map (fn [{:keys [from_node to_nodes] :as value}]
                                                (assoc value :messages
                                                             (sql-conversations tx
                                                               {:sender from_node
                                                                :recipient to_nodes
                                                                :turn (:id active-turn)})))
                                              (->> send-limits
                                                (filter #(and (= (:from_node %) sender)
                                                              (some #{recipient} (:to_nodes %))
                                                              (= (:type %) "shared_send_limit"))))))
          all-shared-rule-limits-exceeded? (every? true?
                                                   (for [entry shared-send-limit-rules]
                                                     (>= (count (:messages entry)) (:value entry))))
          limitless-send-limit-rules (->> send-limits
                                       (filter #(and (= (:from_node %) sender)
                                                     (some #{recipient} (:to_nodes %))
                                                     (= (:type %) "limitless_send_limit"))))
          no-limitless-send-limit-rules? (empty? limitless-send-limit-rules)]

      (when-let [message-character-limit (Integer. (env :message-character-limit))]
        (when (> (count message) message-character-limit)
          (throw (ex-info "Message character limit exceeded" data))))

      (when (and all-rule-limits-exceeded?
                 all-shared-rule-limits-exceeded?
                 no-limitless-send-limit-rules?) ;; limitless_send_limit trumps all
        (throw (ex-info "Message quota exceeded" data)))

      (let [{message-attempt-id :id} (sql-message-attempt-create<! tx data)]
        (binding [*params* (merge (get @rules :default)
                                  (get @rules sender)
                                  {:tx tx
                                   :message message
                                   :sender sender
                                   :message_attempt message-attempt-id
                                   :recipient recipient})]
          (local-eval (:message-execution-schema *params*)))))))

(s/defn message-get [data :- Get] {:post [(s/validate [(assoc Model :is_from_sender Boolean
                                                                    :turn_name String
                                                                    :sender_name String)] %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (sql-message-get tx data)))

(s/defn moderator-messages-get [] {:post [(s/validate [(assoc Model :turn_name String
                                                                    :sender_name String
                                                                    :actual_recipient String
                                                                    :actual_recipient_name String
                                                                    :recipient_name String
                                                                    :message_attempt_deleted Boolean)] %)]}
  (jdbc/with-db-transaction [tx db-spec]
    (sql-moderator-messages tx)))

(s/defn message-delete! [data :- Deletable]
  (jdbc/with-db-transaction [tx db-spec]
    (sql-message-set-deleted<! tx (assoc data :deleted true))))

(s/defn message-undelete! [data :- Undeletable]
  (jdbc/with-db-transaction [tx db-spec]
    (sql-message-set-deleted<! tx (assoc data :deleted false))))

(s/defn message-attempt-delete! [data :- Deletable]
  (jdbc/with-db-transaction [tx db-spec]
    (sql-message-attempt-set-deleted<! tx (assoc data :deleted true))))

(s/defn message-attempt-undelete! [data :- Undeletable]
  (jdbc/with-db-transaction [tx db-spec]
    (sql-message-attempt-set-deleted<! tx (assoc data :deleted false))))