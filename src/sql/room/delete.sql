UPDATE Room
SET deleted = true::boolean
WHERE id = (:id)::bigint;