UPDATE users
SET deleted = true
WHERE id = (:id)::bigint;