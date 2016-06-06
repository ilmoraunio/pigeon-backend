UPDATE users
SET username = (:username)::text,
    full_name = (:full_name)::text,
    password = (:password)::text
WHERE id = (:id)::bigint;