SELECT count(*) > 0 AS is_authorized
FROM Participant
LEFT JOIN users
ON users.username = Participant.username
WHERE Participant.Room_id = (:room_id)::varchar(200)
  AND Participant.username = (:username)::varchar(200)
  AND Participant.deleted = false
  AND users.deleted = false;