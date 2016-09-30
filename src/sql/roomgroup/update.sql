UPDATE RoomGroup
SET room_id = (:room_id)::bigint,
    name = (:name)::varchar(1000),
    parent = (:parent)::bigint,
    users_id = (:users_id):bigint
WHERE id = (:id)::bigint;