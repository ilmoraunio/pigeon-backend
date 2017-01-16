SELECT Room.id,
       Room.name,
       Room.created,
       Room.updated,
       Room.version,
       Room.deleted,
       (EXISTS (SELECT Users.username FROM Participant
                INNER JOIN Users
                ON Users.id = Participant.users_id
                WHERE Room.id = Participant.room_id
                  AND Users.username = (:username)::text)) AS joined
FROM Room
WHERE ((:name)::varchar(1000) IS NULL OR Room.name = (:name)::varchar(1000))
  AND ((:created)::timestamp IS NULL OR Room.created = (:created)::timestamp)
  AND ((:updated)::timestamp IS NULL OR Room.updated = (:updated)::timestamp)
  AND ((:deleted)::boolean IS NULL OR Room.deleted = (:deleted)::boolean)
  --TODO: make ASC/DESC dynamic
ORDER BY Room.created ASC
LIMIT (:limit)::integer OFFSET (:offset)::integer;