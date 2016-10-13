SELECT RoomGroup.id,
       RoomGroup.Room_id,
       RoomGroup.name,
       RoomGroup.parent,
       RoomGroup.users_id,
       RoomGroup.created,
       RoomGroup.updated,
       RoomGroup.version,
       RoomGroup.deleted
FROM RoomGroup
WHERE ((:room_id)::bigint IS NULL OR RoomGroup.Room_id = (:room_id)::bigint)
  AND ((:name)::varchar(1000) IS NULL OR RoomGroup.name = (:name)::varchar(1000))
  AND ((:parent)::bigint IS NULL OR RoomGroup.parent = (:parent)::bigint)
  AND ((:users_id)::bigint IS NULL OR RoomGroup.users_id = (:users_id)::bigint)
  AND ((:created)::timestamp IS NULL OR RoomGroup.created = (:created)::timestamp)
  AND ((:updated)::timestamp IS NULL OR RoomGroup.updated = (:updated)::timestamp)
  AND ((:version)::integer IS NULL OR RoomGroup.version = (:version)::integer)
  AND ((:deleted)::boolean IS NULL OR RoomGroup.deleted = (:deleted)::boolean)
-- TODO order dynamic
ORDER BY RoomGroup.created ASC
LIMIT (:limit)::integer OFFSET (:offset)::integer;