-- name: sql-user-create<!
INSERT INTO users (username, name, password)
VALUES ((:username)::varchar(255), (:name)::varchar(255), (:password)::text)

-- name: sql-user-get
SELECT users.username,
       users.name,
       users.password,
       users.created,
       users.updated,
       users.version,
       users.deleted
FROM users
WHERE ((:username)::varchar(200) IS NULL OR users.username = (:username)::varchar(200))
  AND ((:name)::varchar(200) IS NULL OR users.name = (:name)::varchar(200))
  AND ((:password)::text IS NULL OR users.password = (:password)::text)
  AND ((:created)::timestamp IS NULL OR users.created = (:created)::timestamp)
  AND ((:updated)::timestamp IS NULL OR users.updated = (:updated)::timestamp)
  AND ((:version)::integer IS NULL OR users.version = (:version)::integer)
  AND ((:deleted)::boolean IS NULL OR users.deleted = (:deleted)::boolean)
-- TODO order dynamic
ORDER BY users.created ASC
LIMIT (:limit)::integer OFFSET (:offset)::integer