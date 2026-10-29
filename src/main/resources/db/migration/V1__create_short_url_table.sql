CREATE TABLE short_url (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code         VARCHAR(16)   NOT NULL UNIQUE,
    original_url VARCHAR(2048) NOT NULL,
    clicks       BIGINT        NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now()
);