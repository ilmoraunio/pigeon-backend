(let [tx (:tx *params*)
      message (:message *params*)
      sender (:sender *params*)
      message-attempt-id (:message_attempt *params*)
      recipient (:recipient *params*)
      eavesdroppers (:eavesdroppers params)]
  (if (= (randomize-value 6) 0)
    (doseq [[captor-sender actual-recipient] eavesdroppers]
      (send-message tx {:message message
                        :sender sender
                        :message_attempt message-attempt-id
                        :recipient recipient
                        :actual_recipient actual-recipient})
      (send-message tx {:message message
                        :sender captor-sender
                        :message_attempt message-attempt-id
                        :recipient actual-recipient
                        :actual_recipient actual-recipient}))
    (send-message tx {:message message
                      :sender sender
                      :message_attempt message-attempt-id
                      :recipient recipient
                      :actual_recipient recipient})))