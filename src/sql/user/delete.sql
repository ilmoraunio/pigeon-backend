UPDATE users
SET deleted = true
WHERE username = (:username)::varchar(200);