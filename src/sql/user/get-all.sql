SELECT id, username, full_name, password, deleted
FROM users
WHERE deleted = false;