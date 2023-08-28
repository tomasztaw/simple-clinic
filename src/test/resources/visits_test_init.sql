--INSERT INTO visits (visit_id, doctor_id, patient_id, date_time, note, status)
INSERT INTO visits (doctor_id, patient_id, date_time, note, status)
VALUES
    (1, 1, '2023-06-01 08:30', 'Pacjent bardzo chory', 'odbyta'),
    (2, 2, '2023-06-01 08:30', 'Chore zatoki, zwolnienie L4', 'odbyta'),
    (4, 3, '2023-06-01 10:00', 'Przedawkowanie opiatów', 'odbyta'),
    (4, 4, '2023-06-01 12:30', 'Pacjent symuluje chorobę', 'odbyta'),
    (5, 5, '2023-06-02 08:30', 'Chore nerki, zwolnienie L4', 'odbyta'),
    (4, 6, '2023-06-02 09:30', 'Pacjent bardzo chory, skierowanie do szpitala', 'odbyta'),
    (2, 5, '2023-06-05 08:30', 'Stany lękowe, wymaga leczenia farmakologicznego', 'odbyta'),
    (4, 2, '2023-06-05 11:30', 'Pacjent zdrowy, będzie żył', 'odbyta'),
    (5, 4, '2023-06-05 13:00', 'Chora wątroba, zwolnienie L4', 'odbyta'),
    (2, 5, '2023-06-06 12:30', 'Grzybica stóp', 'odbyta'),
    (3, 5, '2023-06-08 08:00', 'Zapalenie wyrostka', 'odbyta');