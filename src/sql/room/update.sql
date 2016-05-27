UPDATE Room
SET name = (:name)::varchar(1000)
WHERE id = (:id)::bigint;