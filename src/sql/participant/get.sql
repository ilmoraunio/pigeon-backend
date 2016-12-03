SELECT Participant.id,
       Participant.Room_id,
       Participant.name,
       Participant.users_id,
       Participant.created,
       Participant.updated,
       Participant.version,
       Participant.deleted
FROM Participant
WHERE ((:room_id)::bigint IS NULL OR Participant.Room_id = (:room_id)::bigint)
  AND ((:name)::varchar(1000) IS NULL OR Participant.name = (:name)::varchar(1000))
  AND ((:users_id)::bigint IS NULL OR Participant.users_id = (:users_id)::bigint)
  AND ((:created)::timestamp IS NULL OR Participant.created = (:created)::timestamp)
  AND ((:updated)::timestamp IS NULL OR Participant.updated = (:updated)::timestamp)
  AND ((:version)::integer IS NULL OR Participant.version = (:version)::integer)
  AND ((:deleted)::boolean IS NULL OR Participant.deleted = (:deleted)::boolean)
-- TODO order dynamic
ORDER BY Participant.created ASC
LIMIT (:limit)::integer OFFSET (:offset)::integer;