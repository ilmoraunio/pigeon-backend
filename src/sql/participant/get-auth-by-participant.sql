SELECT count(*) > 0 AS is_authorized
FROM Participant
INNER JOIN users
ON users.username = Participant.username
WHERE Participant.Room_id = (:room_id)::text
  AND Participant.username = (:username)::varchar(200)
  AND Participant.id = (:sender)::text
  AND (SELECT count(*) > 0 AS recipient_belongs_to_room
       FROM Room r
       INNER JOIN Participant p
       ON p.Room_id = r.id
       WHERE r.id = (:room_id)::text
         AND p.id = (:recipient)::text)
  AND Participant.deleted = false
  AND users.deleted = false;