INSERT INTO Time (Room_name, name, sequence_order)
VALUES ((:room_name)::varchar(1000), (:name)::varchar(1000), (:sequence_order)::integer);