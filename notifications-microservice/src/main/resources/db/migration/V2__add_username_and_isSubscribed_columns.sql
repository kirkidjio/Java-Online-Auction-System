CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255)
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);

DROP TABLE public.event_publication CASCADE;

ALTER TABLE notifications.email_subscribers
    ADD is_subscribed BOOLEAN;

ALTER TABLE notifications.email_subscribers
    ADD username VARCHAR(255);

ALTER TABLE notifications.email_subscribers
    ALTER COLUMN is_subscribed SET NOT NULL;

ALTER TABLE notifications.email_subscribers
    ALTER COLUMN username SET NOT NULL;

ALTER TABLE notifications.email_subscribers
    ADD CONSTRAINT uc_email_subscribers_username UNIQUE (username);