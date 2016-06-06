INSERT INTO Message (sender_Roomgroup_id,
                     intended_recipient_Roomgroup_id,
                     actual_recipient_Roomgroup_id,
                     body,
                     Room_id,
                     Time_id)
VALUES ((:sender_roomgroup_id)::bigint,
        (:intended_recipient_roomgroup_id)::bigint,
        (:actual_recipient_roomgroup_id)::bigint,
        (:body)::text,
        (:room_id)::bigint,
        (:time_id)::bigint);