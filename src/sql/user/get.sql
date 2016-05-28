SELECT users.id,
       users.username,
       users.full_name,
       users.password,
       users.created,
       users.updated,
       users.version,
       users.deleted
FROM users
WHERE ((:username)::text IS NULL OR users.username = (:username)::text)
  AND ((:full_name)::text IS NULL OR users.full_name = (:full_name)::text)
  AND ((:password)::text IS NULL OR users.password = (:password)::text)
  AND ((:created)::timestamp IS NULL OR users.created = (:created)::timestamp)
  AND ((:updated)::timestamp IS NULL OR users.updated = (:updated)::timestamp)
  AND ((:version)::integer IS NULL OR users.version = (:version)::integer)
  AND ((:deleted)::boolean IS NULL OR users.deleted = (:deleted)::boolean)
-- TODO order dynamic
ORDER BY users.created ASC
LIMIT (:limit)::integer OFFSET (:offset)::integer;