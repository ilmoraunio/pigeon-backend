(let [tx (:tx *params*)
      message (:message *params*)
      sender (:sender *params*)
      message-attempt-id (:message_attempt *params*)
      recipient (:recipient *params*)]
  (send-message tx {:message message
                    :sender sender
                    :message_attempt message-attempt-id
                    :recipient recipient
                    :actual_recipient recipient}))