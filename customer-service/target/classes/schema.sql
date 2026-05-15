CREATE SEQUENCE IF NOT EXISTS customers_customer_id_seq;

CREATE TABLE IF NOT EXISTS customers
(
    customer_id   BIGINT       NOT NULL DEFAULT nextval('customers_customer_id_seq'),
    creation_date TIMESTAMP,
    customer_name VARCHAR(255) NOT NULL,
    dni           VARCHAR(255) NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    last_name     VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT customers_pkey PRIMARY KEY (customer_id)
    );