INSERT INTO Outcome (Property_id, type, value)
VALUES ((:property_id)::integer, (:type)::varchar(1000), (:value)::varchar(1000));