INSERT INTO DirectedConnection (origin, recipient, parent, Time_id)
VALUES ((:origin)::bigint, (:recipient)::bigint, (:parent)::bigint, (:time_id)::bigint);