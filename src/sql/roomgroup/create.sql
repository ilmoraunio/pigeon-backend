INSERT INTO RoomGroup (Room_id, name, parent, users_id)
VALUES ((:room_id)::bigint, (:name)::varchar(1000), (:parent)::bigint, (:users_id)::bigint);