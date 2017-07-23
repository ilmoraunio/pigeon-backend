-- name: sql-get-send-limit
SELECT from_node, to_nodes, type, value
  FROM send_limit;