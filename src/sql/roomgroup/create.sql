INSERT INTO RoomGroup (Room_name, name, parent)
VALUES ((:room_name)::varchar(1000), (:name)::varchar(1000), (:parent)::integer);