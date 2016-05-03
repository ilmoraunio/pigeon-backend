INSERT INTO Property (DirectedConnection_id, name, type, value)
VALUES ((:directedconnectionid)::integer, (:name)::varchar(1000), (:type)::varchar(1000), (:value)::varchar(1000));