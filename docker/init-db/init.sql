CREATE DATABASE kafka;

CREATE TABLE IF NOT EXISTS clients
(
    client_id BIGINT PRIMARY KEY,
    email     VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions
(
    transaction_id   SERIAL PRIMARY KEY,
    bank             VARCHAR(255)     NOT NULL,
    client_id        BIGINT           NOT NULL,
    transaction_type VARCHAR(50)      NOT NULL,
    created_at       TIMESTAMP        NOT NULL,
    total_cost       DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (client_id) REFERENCES clients (client_id)
)