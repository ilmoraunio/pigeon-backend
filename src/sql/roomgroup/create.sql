INSERT INTO RoomGroup (Room_id, name, users_id)
VALUES ((:room_id)::bigint, (:name)::varchar(1000), (:users_id)::bigint);