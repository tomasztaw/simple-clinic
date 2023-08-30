INSERT INTO doctors (name, surname, title, email, phone)
VALUES ('Alojzy', 'Kowalski', 'Laryngolog', 'alojzkowalski@eclinic.pl', '+48 120 121 122'),
('Anna', 'Nowak', 'Kardiolog', 'annanowak@eclinic.pl', '+48 120 130 140'),
('Kornel', 'Makuszyński', 'Lekarz rodzinny', 'kornel@eclinic.pl', '+48 120 130 142'),
('Jadwiga', 'Kuszyńska', 'Pediatra', 'jkuszynska@eclinic.pl', '+48 120 130 148'),
('Wacław', 'Piątkowski', 'Gastrolog', 'wacek@eclinic.pl', '+48 120 130 150'),
('Aleksander', 'Newski', 'Gastrolog', 'oleknew@eclinic.pl', '+48 120 130 152'),
('Urszula', 'Nowakowska', 'Lekarz rodzinny', 'ulala@eclinic.pl', '+48 120 130 158'),
('Paweł', 'Stękała', 'Podolog', 'p.stekala@eclinic.pl', '+48 120 130 160'),
('Magdalena', 'Kraska', 'Ortopeda', 'm.kraska@eclinic.pl', '+48 120 130 161'),
('Joanna', 'Wojtyniak', 'Chirurg', 'j.wojtyniak@eclinic.pl', '+48 120 130 162'),
('Małgorzata', 'Figurska', 'Neurolog', 'm.figurska@eclinic.pl', '+48 120 130 163'),
('Kornel', 'Kaleta', 'Okulista', 'k.kaleta@eclinic.pl', '+48 120 130 164'),
('Adam', 'Suchy', 'Urolog', 'a.suchy@eclinic.pl', '+48 120 130 165'),
('Feliks', 'Makowski', 'Kardiolog', 'f.mak@eclinic.pl', '+48 120 130 166'),
('Konrad', 'Zalewski', 'Dermatolog', 'k.zalewski@eclinic.pl', '+48 120 130 167');

INSERT INTO patients (name, surname, pesel, email, phone)
VALUES ('Agata', 'Andrzejewska', '70011012345', 'aa@gmail.com', '+48 220 221 222'),
('Wojciech', 'Suchodolski', '72072514777', 'suchy@gmail.com', '+48 258 369 147'),
('Stefan', 'Zajavka', '72072514725', 'zajavka@zajavka.com', '+48 548 664 441'),
('Agnieszka', 'Spring', '85061718342', 'aga@zajavka.com', '+48 548 115 312'),
('Tomasz', 'Hibernate', '85061718378', 'hibertomasz@zajavka.com', '+48 548 656 565'),
('Bogumił', 'Kononowicz', '59031118222', 'bogutek@gmail.com', '+48 258 369 999'),
('Borys', 'Niewolski', '58032018233', 'b.niewolski@yahoo.com', '+48 258 369 998'),
('Alina', 'Sabińska', '78061185622', 'a.sabinska@gmail.com', '+48 258 369 997'),
('Krystian', 'Czyżowicz', '61081918277', 'k.czyzowicz@interia.pl', '+48 258 369 996'),
('Piotr', 'Kowalewski', '91070113692', 'p.kowal@gmail.com', '+48 258 369 995'),
('Adam', 'Nowak', '55041517723', 'adam.nowak@wp.pl', '+48 258 369 994'),
('Marek', 'Młynarski', '58111217452', 'mm@gmail.com', '+48 258 369 993'),
('Bartosz', 'Konieczny', '78092516212', 'b.konieczny@gmail.com', '+48 258 369 991'),
('Anna', 'Pyziak', '92120517244', 'a.pyziak@wp.pl', '+48 258 369 990'),
('Lucjan', 'Niezdatny', '65050114569', 'lucek@gmail.com', '+48 258 369 989'),
('Arkadiusz', 'Malinowski', '88040217522', 'a.malinowski@gmail.com', '+48 258 369 988'),
('Jagoda', 'Maliniak', '89012518128', 'jagoda.malina@gmail.com', '+48 258 369 987'),
('Klaudia', 'Szymańska', '71021114722', 'k.szym@gmail.com', '+48 258 369 986'),
('Bogumił', 'Baranowicz', '73102218266', 'b.baran@gmail.com', '+48 258 369 985'),
('Krystyna', 'Sas', '82083015726', 'k.sas@internia.pl', '+48 258 369 984'),
('Bożydar', 'Nowak', '55102317221', 'bozydar.nowak@gmail.com', '+48 258 369 983');

INSERT INTO visits (doctor_id, patient_id, date_time, note, status)
VALUES (1, 1, '2023-06-01 08:30:00', 'Pacjent bardzo chory', 'odbyta'),
(2, 2, '2023-06-01 08:30:00', 'Chore zatoki, zwolnienie L4', 'odbyta'),
(3, 3, '2023-06-01 10:00:00', 'Przedawkowanie opiatów', 'odbyta'),
(4, 4, '2023-06-01 12:30:00', 'Pacjent symuluje chorobę', 'odbyta'),
(5, 5, '2023-06-02 08:30:00', 'Chore nerki, zwolnienie L4', 'odbyta'),
(6, 6, '2023-06-02 09:30:00', 'Pacjent bardzo chory, skierowanie do szpitala', 'odbyta'),
(7, 5, '2023-06-05 08:30:00', 'Stany lękowe, wymaga leczenia farmakologicznego', 'odbyta'),
(1, 2, '2023-06-05 11:30:00', 'Pacjent zdrowy, będzie żył', 'odbyta'),
(2, 4, '2023-06-05 13:00:00', 'Chora wątroba, zwolnienie L4', 'odbyta'),
(4, 5, '2023-06-06 12:30:00', 'Grzybica stóp', 'odbyta'),
(8, 20, '2023-06-12 08:10:00', 'Ból zęba, pruchnica i tak dalej', 'odbyta'),
(9, 15, '2023-06-08 10:40:00', 'Ściągnięcie gipsu, złamanie się zrosło', 'odbyta'),
(10, 11, '2023-06-09 10:00:00', 'Skierowanie do szpitala na operację usunięcia ślepej kiszki', 'odbyta'),
(10, 12, '2023-06-09 10:20:00', 'Badanie EKG', 'odbyta'),
(11, 5, '2023-06-08 14:00:00', 'Wszystko w porządku, bez komplikacji', 'odbyta'),
(11, 20, '2023-06-08 14:30:00', 'Skierowanie na dalsze badania', 'odbyta'),
(12, 9, '2023-06-09 10:00:00', 'Jaskra, wymaga pilnego leczenia', 'odbyta'),
(12, 10, '2023-06-09 10:10:00', 'Daltonizm skrajny', 'odbyta'),
(12, 11, '2023-06-09 10:30:00', 'Potrzebne są okulary z większymi szkłami', 'odbyta'),
(13, 5, '2023-06-09 08:00:00', 'Standardowe badania', 'odbyta'),
(13, 8, '2023-06-09 08:10:00', 'Wizyta kontrolna', 'odbyta'),
(13, 14, '2023-06-09 09:10:00', 'Pacjent popuszcza', 'odbyta'),
(13, 18, '2023-06-09 10:30:00', 'Czekamy na wyniki badań', 'odbyta'),
(14, 5, '2023-06-09 09:10:00', 'Problemy z sercem i ciśnieniem', 'odbyta'),
(14, 15, '2023-06-09 09:30:00', 'Wszystko w porządku', 'odbyta'),
(14, 20, '2023-06-09 11:30:00', 'Wysokie ciśnienie', 'odbyta'),
(15, 5, '2023-06-12 10:00:00', 'Problemy skórne, wypryski', 'odbyta'),
(15, 2, '2023-06-12 10:10:00', 'Poparzenie słoneczne', 'odbyta'),
(15, 4, '2023-06-12 10:20:00', 'Czerniak złośliwy', 'odbyta'),
(5, 3, '2023-06-13 12:00:00', 'Wizyta kontrolna, nic się nie dzieje', 'odbyta'),
(6, 11, '2023-06-15 09:00:00', 'Zlecenie badań diagnostycznych', 'odbyta'),
(7, 19, '2023-06-19 10:00:00', 'Pacjent zdrowy jak ryba', 'odbyta'),
(8, 18, '2023-06-20 10:00:00', 'Dolna szczena cała do leczenia', 'odbyta');

INSERT INTO opinions (doctor_id, patient_id, visit_id, comment, created_at)
VALUES (1, 1, 1,'Bardzo dobry lekarz, polecam', '2023-06-01 12:45:00'),
(1, 2, 8, 'Wszystko super, polecam', '2023-06-05 13:45:00'),
(6, 6, 6, 'Słabo, lekarz chyba pijany', '2023-06-02 16:45:00'),
(5, 5, 5, 'Bardzo dobry lekarz, elegancko', '2023-06-02 12:45:00'),
(7, 5, 7, 'Chłop wystawia lewe L4', '2023-06-05 12:45:00'),
(2, 4, 9, 'Wszystko OK', '2023-06-05 13:45:00'),
(4, 5, 10, 'Olewatorskie podejście', '2023-06-06 18:45:00'),
(8, 20, 11, 'Polecam tego stomatologa', '2023-06-12 14:00:00'),
(9, 15, 12, 'Polecam panią doktor, świetny spacjalista', '2023-06-08 15:00:00'),
(10, 12, 14, 'Badanie szybko, sprawnie, bezboleśnie', '2023-06-10 12:00:00'),
(11, 20, 16, 'Gorąco polecam', '2023-06-08 20:00:00'),
(12, 10, 18, 'Pani doktor jest świetnym okulistą, polecam', '2023-06-09 20:00:00'),
(13, 8, 21, 'Wszystko super, polecam', '2023-06-09 18:00:00'),
(13, 14, 22, 'Bardzo polecam tego urologa', '2023-06-09 20:00:00'),
(14, 15, 25, 'W porządku lekarz, polecam', '2023-06-09 21:00:00'),
(15, 2, 28, 'Miło i fachowo', '2023-06-12 18:00:00'),
(5, 3, 30, 'Polecam', '2023-06-13 18:00:00'),
(6, 11, 31, 'Polecam bardzo tego lekarza', '2023-06-15 19:00:00'),
(7, 19, 32, 'Wszystko ok, bardzo polecam', '2023-06-19 21:00:00'),
(7, 19, 32, 'Wszystko ok, bardzo polecam', '2023-06-19 21:00:00'),
(8, 18, 33, 'Mam wrażenie, że chłop chce mnie naciągnąć', '2023-06-12 22:00:00'),

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (1, 1, '08:00', '13:00'),
(1, 2, '09:00', '12:00'),
(1, 3, '08:00', '13:00'),
(1, 4, '09:00', '12:00'),
(1, 5, '08:00', '13:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (2, 1, '09:00', '14:00'),
(2, 2, '09:00', '14:00'),
(2, 3, '09:00', '14:00'),
(2, 4, '09:00', '14:00'),
(2, 5, '09:00', '14:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (3, 1, '08:00', '12:00'),
(3, 2, '08:00', '12:00'),
(3, 3, '08:00', '12:00'),
(3, 4, '08:00', '12:00'),
(3, 5, '08:00', '12:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (4, 1, '08:00', '11:00'),
(4, 3, '08:00', '11:00'),
(4, 5, '08:00', '11:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (5, 2, '12:00', '15:00'),
(5, 4, '12:00', '15:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (6, 4, '09:00', '15:00'),
(6, 5, '08:00', '15:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (7, 1, '10:00', '17:00'),
(7, 3, '10:00', '17:00'),
(7, 5, '10:00', '17:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (8, 1, '08:00', '11:00'),
(8, 2, '08:00', '11:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (9, 3, '10:00', '15:00'),
(9, 4, '10:00', '15:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (10, 1, '10:00', '14:00'),
(10, 3, '10:00', '14:00'),
(10, 5, '10:00', '14:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (11, 3, '14:00', '16:00'),
(11, 4, '14:00', '16:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (12, 1, '10:00', '12:00'),
(12, 2, '10:00', '12:00'),
(12, 3, '10:00', '12:00'),
(12, 4, '10:00', '12:00'),
(12, 5, '10:00', '12:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (13, 2, '08:00', '11:00'),
(13, 5, '08:00', '11:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (14, 5, '08:00', '14:00');

INSERT INTO doctors_schedule (doctor_id, day_of_week, start_time_ds, end_time_ds)
VALUES (15, 1, '10:00', '12:00'),
(15, 2, '10:00', '12:00'),
(15, 5, '10:00', '12:00');

