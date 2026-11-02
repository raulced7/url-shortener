ALTER TABLE short_url ADD COLUMN expires_at TIMESTAMPTZ;

CREATE INDEX idx_short_url_expires_at
    ON short_url (expires_at)
    WHERE expires_at IS NOT NULL;