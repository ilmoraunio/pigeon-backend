UPDATE users
SET full_name = (:full_name)::varchar(200),
    password = (:password)::text
WHERE username = (:username)::text;