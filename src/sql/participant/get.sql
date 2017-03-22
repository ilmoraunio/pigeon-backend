SELECT Participant.id,
       Participant.Room_id,
       Participant.name,
       Participant.username,
       Participant.created,
       Participant.updated,
       Participant.version,
       Participant.deleted
FROM Participant
WHERE ((:room_id)::text IS NULL OR Participant.Room_id = (:room_id)::text)
  AND ((:name)::varchar(200) IS NULL OR Participant.name = (:name)::varchar(200))
  AND ((:username)::varchar(200) IS NULL OR Participant.username = (:username)::varchar(200))
  AND ((:created)::timestamp IS NULL OR Participant.created = (:created)::timestamp)
  AND ((:updated)::timestamp IS NULL OR Participant.updated = (:updated)::timestamp)
  AND ((:version)::integer IS NULL OR Participant.version = (:version)::integer)
  AND ((:deleted)::boolean IS NULL OR Participant.deleted = (:deleted)::boolean)
-- TODO order dynamic
ORDER BY Participant.created ASC
LIMIT (:limit)::integer OFFSET (:offset)::integer;