CREATE TABLE doctors (
    doctor_id SERIAL          PRIMARY KEY,
    name      VARCHAR(20)     NOT NULL,
    surname   VARCHAR(20)     NOT NULL,
    title     VARCHAR(20)     NOT NULL,
    phone     VARCHAR(32)     NOT NULL UNIQUE,
    email     VARCHAR(32)     NOT NULL UNIQUE
);

CREATE TABLE patients (
    patient_id SERIAL          PRIMARY KEY,
    name       VARCHAR(20)     NOT NULL,
    surname    VARCHAR(20)     NOT NULL,
    pesel      VARCHAR(20)     NOT NULL UNIQUE,
    phone      VARCHAR(32)     NOT NULL UNIQUE,
    email      VARCHAR(32)     NOT NULL UNIQUE
);

CREATE TABLE visits (
  visit_id   SERIAL      PRIMARY KEY,
  doctor_id  INT         REFERENCES doctors (doctor_id),
  patient_id INT         REFERENCES patients (patient_id),
  date_time  TIMESTAMP                                    NOT NULL,
  note       TEXT                                         NOT NULL,
  status     VARCHAR(64)                                  NOT NULL
);

CREATE TABLE doctors_schedule (
    id            SERIAL PRIMARY KEY,
    doctor_id     INT    REFERENCES doctors (doctor_id) NOT NULL,
    day_of_week   INT                                   NOT NULL,
    start_time_ds TIME                                  NOT NULL,
    end_time_ds   TIME                                  NOT NULL
);

CREATE TABLE opinions (
    opinion_id SERIAL    PRIMARY KEY,
    doctor_id  INT       REFERENCES doctors (doctor_id)   NOT NULL,
    patient_id INT       REFERENCES patients (patient_id) NOT NULL,
    visit_id   INT       REFERENCES visits (visit_id),
    comment    TEXT                                       NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reservations (
    reservation_id SERIAL  PRIMARY KEY,
    doctor_id      INT     REFERENCES doctors (doctor_id)   NOT NULL,
    patient_id     INT     REFERENCES patients (patient_id) NOT NULL,
    day            DATE                                     NOT NULL,
    start_time_r   TIME                                     NOT NULL,
    occupied       BOOLEAN DEFAULT false
);


