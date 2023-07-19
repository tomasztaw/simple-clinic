CREATE TABLE pet (
    id               SERIAL      NOT NULL,
    pet_store_pet_id BIGINT      NOT NULL,
    name             VARCHAR(64) NOT NULL,
    status           VARCHAR(64) NOT NULL,
    patient_id       INT         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_pet_patient
        FOREIGN KEY (patient_id)
            REFERENCES patients (patient_id)
);