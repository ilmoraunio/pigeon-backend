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
             (SELECT turn.id from turn where active = true and deleted = false));

-- name: sql-message-get
      SELECT message.id,
             message.sender,
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
               AND message.recipient = (:recipient)::varchar(255))
           OR (message.sender = (:recipient)::varchar(255)
               AND message.recipient = (:sender)::varchar(255)))
         AND message.deleted = false
    ORDER BY turn.ordering ASC,
             message.created ASC;

-- name: sql-conversations
SELECT *
  FROM message
 WHERE ((:sender)::varchar(255) IS NULL OR message.sender = (:sender)::varchar(255))
   AND ((:turn)::integer IS NULL OR message.turn = (:turn)::integer)
   AND message.recipient IN (:recipient)