CREATE SEQUENCE IF NOT EXISTS users_user_id_seq;

CREATE TABLE IF NOT EXISTS users
(
    user_id       BIGINT       NOT NULL DEFAULT nextval('users_user_id_seq'),
    creation_date TIMESTAMP,
    password      VARCHAR(255) NOT NULL,
    role          VARCHAR(255) NOT NULL,
    username      VARCHAR(255) NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (user_id),
    CONSTRAINT users_username_unique UNIQUE (username),
    CONSTRAINT users_role_check CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'))
    );