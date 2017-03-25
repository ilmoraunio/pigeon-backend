SELECT count(*) > 0 AS is_authorized
FROM Participant
INNER JOIN users
ON users.username = Participant.username
WHERE Participant.Room_id = (:room_id)::text
  AND Participant.username = (:username)::varchar(200)
  AND Participant.id = (:participant)::text
  AND Participant.deleted = false
  AND users.deleted = false;