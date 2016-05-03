INSERT INTO Time (Room_name, name, sequence_order)
VALUES ((:roomname)::varchar(1000), (:name)::varchar(1000), (:sequenceorder)::integer);