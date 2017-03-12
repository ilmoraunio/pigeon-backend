INSERT INTO Participant (Room_id, name, username)
VALUES ((:room_id)::text, (:name)::varchar(200), (:username)::varchar(200));