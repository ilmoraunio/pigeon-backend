UPDATE RoomGroup
SET deleted = true
WHERE id = (:id)::bigint;