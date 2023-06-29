INSERT INTO app_user (user_name, email, password, name, active)
VALUES ('admin', 'admin@eclinic.pl', '$2a$12$naVplXarXH8dpoOoRcidsechj1DOkqf7YM2GJ2jXLhga7ALRmdc/W', 'Admin', true),
('doctor1', 'doctor1@eclinic.pl', '$2a$12$/LdurcSunNFdLczO.44eI.UidCUa9AHSVU5Unyg/dJoztwwwhUpoy', 'Doktor', true),
('patient1', 'patient1@gmail.com', '$2a$12$qgMWMwjycm6HYWC05MBCEOPqevGmEZwDT5aPej1FD3s.PST1sogJe', 'Pacjent', true);

INSERT INTO app_role (role) VALUES ('ADMIN'), ('USER'), ('DOCTOR');

INSERT INTO app_user_role (user_id, role_id) VALUES (1, 1), (2, 2), (3, 3);