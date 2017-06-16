INSERT INTO users (username, name, password)
VALUES ((:username)::varchar(200), (:name)::varchar(200), (:password)::text);