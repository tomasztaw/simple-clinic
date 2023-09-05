DROP TABLE IF EXISTS app_user_role CASCADE;
DROP TABLE IF EXISTS app_role CASCADE;
DROP TABLE IF EXISTS app_user CASCADE;

DROP TABLE IF EXISTS doctors_schedule CASCADE;
DROP TABLE IF EXISTS pet CASCADE;
DROP TABLE IF EXISTS reservations CASCADE;
DROP TABLE IF EXISTS prescriptions CASCADE;
DROP TABLE IF EXISTS opinions CASCADE;
DROP TABLE IF EXISTS visits CASCADE;
DROP TABLE IF EXISTS doctors CASCADE;
DROP TABLE IF EXISTS patients CASCADE;
DROP TABLE IF EXISTS flyway_schema_history;

CREATE TABLE flyway_schema_history (
  installed_rank INT NOT NULL,
  version VARCHAR(50),
  description VARCHAR(200) NOT NULL,
  type VARCHAR(20) NOT NULL,
  script VARCHAR(1000) NOT NULL,
  checksum INT,
  installed_by VARCHAR(100) NOT NULL,
  installed_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  execution_time INT NOT NULL,
  success BOOLEAN NOT NULL
);
