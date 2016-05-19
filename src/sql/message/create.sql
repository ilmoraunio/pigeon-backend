INSERT INTO Message (sender_Roomgroup_id,
                     intended_recipient_Roomgroup_id,
                     actual_recipient_Roomgroup_id,
                     body,
                     Room_name,
                     Time_Room_name,
                     Time_name)
VALUES ((:sender_roomgroup_id)::integer,
        (:intended_recipient_roomgroup_id)::integer,
        (:actual_recipient_roomgroup_id)::integer,
        (:body)::text,
        (:room_name)::varchar(1000),
        (:time_room_name)::varchar(1000),
        (:time_name)::varchar(1000));