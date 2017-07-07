-- name: sql-get-rule
  SELECT ref,
         order_no,
         short_circuit_rule_chain_if_true,
         short_circuit_rule_chain_if_false,
         type,
         from_node,
         to_node,
         value,
         if_satisfied_then_direct_to_nodes
    FROM rule
   WHERE to_node = (:recipient)::varchar(255)
ORDER BY order_no;