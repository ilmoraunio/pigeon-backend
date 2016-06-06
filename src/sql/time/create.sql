INSERT INTO Time (Room_id, name, sequence_order)
VALUES ((:room_id)::bigint, (:name)::varchar(1000), (:sequence_order)::integer);