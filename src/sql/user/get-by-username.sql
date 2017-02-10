SELECT username, full_name, password, created, updated, version, deleted
FROM users
WHERE username = (:username)::varchar(200) AND deleted = false;