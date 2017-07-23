-- name: sql-turn-get
SELECT id, name, ordering, active, deleted, created, updated, version
  FROM turn
 WHERE deleted = false
ORDER BY ordering, name

-- name: sql-inactivate-turn<!
UPDATE turn
   SET active = false
 WHERE active = true

-- name: sql-activate-turn<!
UPDATE turn
   SET active = true
 WHERE id = (:id)::integer