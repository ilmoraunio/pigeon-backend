UPDATE Participant
SET deleted = true
WHERE id = (:id)::bigint;