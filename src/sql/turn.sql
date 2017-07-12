-- name: sql-turn-get
SELECT id, name, ordering, active, deleted, created, updated, version
  FROM turn
 WHERE deleted = false