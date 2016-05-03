INSERT INTO users (username, full_name, password)
VALUES ((:username)::text, (:full_name)::text, (:password)::text);