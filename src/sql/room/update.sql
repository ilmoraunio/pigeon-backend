UPDATE Room
SET name = (:name)::varchar(1000)
WHERE name = (:id)::varchar(1000);