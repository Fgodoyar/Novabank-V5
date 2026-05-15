DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    customer_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100)  NOT NULL,
    last_name     VARCHAR(100)  NOT NULL,
    dni           VARCHAR(20)   NOT NULL UNIQUE,
    email         VARCHAR(150)  NOT NULL UNIQUE,
    phone_number  VARCHAR(20)   NOT NULL UNIQUE,
    creation_date TIMESTAMP
);