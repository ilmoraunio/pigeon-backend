INSERT INTO users (username, full_name, password)
VALUES ((:username)::varchar(200), (:full_name)::varchar(200), (:password)::text);