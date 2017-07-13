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
             (SELECT turn.id
                FROM turn
               WHERE active = true
                 AND deleted = false));

-- name: sql-message-attempt-create<!
INSERT INTO message_attempt (sender,
                             recipient,
                             turn)
     VALUES ((:sender)::varchar(255),
             (:recipient)::varchar(255),
             (SELECT turn.id
                FROM turn
               WHERE active = true
                 AND deleted = false));

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
  INNER JOIN (SELECT from_node, to_node
                FROM (SELECT from_node, unnest(to_nodes) AS to_node
                        FROM visibility) as _) AS visibility
          ON visibility.from_node = (:sender)::varchar(255)
         AND visibility.to_node = (:recipient)::varchar(255)
       WHERE ((message.sender = (:sender)::varchar(255)
           AND message.recipient = (:recipient)::varchar(255))
          OR (message.sender = (:recipient)::varchar(255)
          AND message.actual_recipient = (:sender)::varchar(255)))
         AND message.deleted = false
    ORDER BY turn.ordering ASC,
             message.created ASC;

-- name: sql-conversations
SELECT *
  FROM message_attempt
 WHERE ((:sender)::varchar(255) IS NULL OR message_attempt.sender = (:sender)::varchar(255))
   AND ((:turn)::integer IS NULL OR message_attempt.turn = (:turn)::integer)
   AND message_attempt.recipient IN (:recipient)
   AND message_attempt.deleted = false

-- name: sql-message-set-deleted<!
UPDATE message
   SET deleted = (:deleted)::boolean
 WHERE id = (:id)::integer