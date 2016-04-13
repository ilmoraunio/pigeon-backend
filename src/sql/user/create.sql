INSERT INTO users (id, username, full_name, password)
VALUES (DEFAULT, (:username)::text, (:full_name)::text, (:password)::text);