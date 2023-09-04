INSERT INTO app_user (user_id, user_name, email, password, name, active)
VALUES (1, 'admin', 'admin@eclinic.pl', '$2a$12$naVplXarXH8dpoOoRcidsechj1DOkqf7YM2GJ2jXLhga7ALRmdc/W', 'Admin', true),
(2, 'doctor1', 'doctor1@eclinic.pl', '$2a$12$/LdurcSunNFdLczO.44eI.UidCUa9AHSVU5Unyg/dJoztwwwhUpoy', 'Doktor', true),
(3, 'patient1', 'patient1@gmail.com', '$2a$12$qgMWMwjycm6HYWC05MBCEOPqevGmEZwDT5aPej1FD3s.PST1sogJe', 'Pacjent', true),
(4, 'tomek', 'hibertomasz@zajavka.com', '$2a$12$3Wtgj90g3Lb7VQPMX30gLOo7./7a0K9RtHB/3xot7h2Ubta6c.ESi', 'Tomasz', true);

--(5, 'alojz', 'alojz@eclinic.pl', '$2a$12$3Wtgj90g3Lb7VQPMX30gLOo7./7a0K9RtHB/3xot7h2Ubta6c.ESi', 'Alojzy', true);
--(6, 'konrad', 'k.zalewski@eclinic.pl', '$2a$12$3Wtgj90g3Lb7VQPMX30gLOo7./7a0K9RtHB/3xot7h2Ubta6c.ESi', 'Tomasz', true);

INSERT INTO app_role (role_id, role) VALUES (1, 'ADMIN'), (2, 'DOCTOR'), (3, 'USER');

INSERT INTO app_user_role (user_id, role_id) VALUES (1, 1), (2, 2), (3, 3), (4, 3);

--(5, 2), (6, 2)



--INSERT INTO app_role (id, role) VALUES (1, 'ADMIN'), (2, 'DOCTOR'), (3, 'USER') ON CONFLICT DO NOTHING;
--
--INSERT INTO app_user (user_id, user_name, email, password, name, active)
--VALUES (1, 'admin', 'admin@eclinic.pl', '$2a$12$naVplXarXH8dpoOoRcidsechj1DOkqf7YM2GJ2jXLhga7ALRmdc/W', 'Admin', true),
--(2, 'doctor1', 'doctor1@eclinic.pl', '$2a$12$/LdurcSunNFdLczO.44eI.UidCUa9AHSVU5Unyg/dJoztwwwhUpoy', 'Doktor', true),
--(3, 'patient1', 'patient1@gmail.com', '$2a$12$qgMWMwjycm6HYWC05MBCEOPqevGmEZwDT5aPej1FD3s.PST1sogJe', 'Pacjent', true);
--
--INSERT INTO app_role (role_id, role) VALUES (1, 'ADMIN'), (2, 'USER'), (3, 'DOCTOR');
--
--INSERT INTO app_user_role (user_id, role_id) VALUES (1, 1), (2, 2), (3, 3);