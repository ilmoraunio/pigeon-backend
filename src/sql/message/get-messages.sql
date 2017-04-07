SELECT Message.id,
       Message.Room_id,
       Message.sender,
       (SELECT name
        FROM Participant
        WHERE Participant.id = Message.sender) AS sender_name,
       Message.recipient,
       Message.message,
       Message.sender = (:sender)::text AS is_from_sender,
       Message.created,
       Message.updated,
       Message.version,
       Message.deleted
FROM Message
WHERE Message.Room_id = (:room_id)::text
  AND ((Message.sender = (:sender)::text
        AND Message.recipient = (:recipient)::text)
      OR (Message.sender = (:recipient)::text
          AND Message.recipient = (:sender)::text))
  AND Message.deleted = (:deleted)::boolean
ORDER BY Message.created ASC
LIMIT (:limit)::integer OFFSET (:offset)::integer;