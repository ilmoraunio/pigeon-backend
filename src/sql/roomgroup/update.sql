UPDATE RoomGroup
SET room_id = (:room_id)::bigint,
    name = (:name)::varchar(1000),
    users_id = (:users_id)::bigint
WHERE id = (:id)::bigint;