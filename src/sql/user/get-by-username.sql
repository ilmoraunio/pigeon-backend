SELECT id, username, full_name, password, created, updated, version, deleted
FROM users
WHERE username = (:username)::text AND deleted = false;