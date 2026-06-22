CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE fintechs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150) NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE customers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_type   VARCHAR(10)  NOT NULL,
    document_number VARCHAR(30)  NOT NULL,
    name            VARCHAR(150) NOT NULL,
    email           VARCHAR(150),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_customer_document UNIQUE (document_type, document_number)
);

CREATE TABLE bank_accounts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id     UUID         NOT NULL REFERENCES customers(id),
    bank_code       VARCHAR(10)  NOT NULL,
    account_type    VARCHAR(15)  NOT NULL,
    account_number  VARCHAR(30)  NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_bank_account UNIQUE (customer_id, bank_code, account_number)
);

CREATE TABLE validations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id      UUID         NOT NULL REFERENCES bank_accounts(id),
    request_id      VARCHAR(80)  NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ach_reference   VARCHAR(60),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_validation_request UNIQUE (request_id)
);

CREATE TABLE webhook_events (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id        VARCHAR(80)  NOT NULL,
    ach_reference   VARCHAR(60)  NOT NULL,
    payload         TEXT         NOT NULL,
    received_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_webhook_event UNIQUE (event_id)
);