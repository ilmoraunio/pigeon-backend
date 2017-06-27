-- name: sql-message-create<!
INSERT INTO message (sender,
                     recipient,
                     actual_recipient,
                     message,
                     turn)
     VALUES ((:sender)::varchar(255),
             (:recipient)::varchar(255),
             (:actual_recipient)::varchar(255),
             (:message)::text,
             (:turn)::integer);

-- name: sql-message-get
      SELECT message.sender,
             message.recipient,
             message.actual_recipient,
             message.message,
             message.turn,
             message.created,
             message.updated,
             message.version,
             message.deleted,
             (message.sender = (:sender)::varchar(255)) as is_from_sender,
             (SELECT name FROM users where username = message.sender) as sender_name,
             turn.name as turn_name
        FROM message
  INNER JOIN turn
          ON turn.id = message.turn
       WHERE ((message.sender = (:sender)::varchar(255)
               AND message.actual_recipient = (:recipient)::varchar(255))
           OR (message.sender = (:recipient)::varchar(255)
               AND message.actual_recipient = (:sender)::varchar(255)))
         AND message.deleted = false
    ORDER BY turn.ordering ASC,
             message.created ASC;