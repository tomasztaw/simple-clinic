--INSERT INTO reservations (reservation_id, doctor_id, patient_id, day, start_time_r, occupied)
INSERT INTO reservations (doctor_id, patient_id, day, start_time_r, occupied)
VALUES
    (1, 1, '2023-08-23', '09:00', true),
    (2, 2, '2023-08-24', '10:00', true),
    (3, 2, '2023-08-24', '11:00', true),
    (4, 5, '2023-08-25', '12:00', true),
    (1, 5, '2023-08-25', '10:00', true),
    (2, 5, '2023-08-25', '12:00', true);

--    (101, 1, 1, '2023-08-23', '09:00', true),
--    (102, 2, 2, '2023-08-24', '10:00', true),
--    (103, 3, 2, '2023-08-24', '11:00', true),
--    (104, 4, 5, '2023-08-25', '12:00', true),
--    (105, 1, 5, '2023-08-25', '10:00', true),
--    (106, 2, 5, '2023-08-25', '12:00', true);

--reservation_date