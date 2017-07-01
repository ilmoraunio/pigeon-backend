-- name: get-migrations
SELECT id, created_at
FROM ragtime_migrations

-- name: get-table-names
SELECT table_name
  FROM information_schema.tables
 WHERE table_schema = 'public'
   AND table_type = 'BASE TABLE'

-- name: get-table-names-without-meta
SELECT table_name
  FROM information_schema.tables
 WHERE table_schema = 'public'
   AND table_name != 'ragtime_migrations'
   AND table_type = 'BASE TABLE'