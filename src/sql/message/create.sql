INSERT INTO Message (Room_id, sender, recipient, message)
VALUES ((:room_id)::text, (:sender)::text, (:recipient)::text, (:message)::text);