INSERT INTO RoomGroup (Room_id, name, parent)
VALUES ((:room_id)::bigint, (:name)::varchar(1000), (:parent)::bigint);