SELECT id, username, full_name, password, deleted
FROM users
WHERE username = (:username)::text AND deleted = false;