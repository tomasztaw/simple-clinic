CREATE TABLE app_user (
    user_id     SERIAL       NOT NULL,
    user_name   VARCHAR(20)  NOT NULL,
    email       VARCHAR(32)  NOT NULL,
    password    VARCHAR(256) NOT NULL,
    user_name   VARCHAR(20)  NOT NULL,
    active      BOOLEAN      NOT NULL,
    PRIMARY KEY (user_id),
    UNIQUE (user_name),
    UNIQUE (email)
);

CREATE TABLE app_role (
    role_id SERIAL      NOT NULL,
    role    VARCHAR(20) NOT NULL,
    PRIMARY KEY (role_id)
);

CREATE TABLE app_user_role (
    user_id INT REFERENCES (user_id) NOT NULL,
    role_id INT REFERENCES (role_id) NOT NULL,
    PRIMARY KEY (user_id, role_id)
);