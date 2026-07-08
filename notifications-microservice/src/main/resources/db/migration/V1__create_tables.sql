CREATE TABLE email_subscribers
(
    id      UUID         NOT NULL,
    user_id UUID         NOT NULL,
    email   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_email_subscribers PRIMARY KEY (id)
);

ALTER TABLE email_subscribers
    ADD CONSTRAINT uc_email_subscribers_email UNIQUE (email);

ALTER TABLE email_subscribers
    ADD CONSTRAINT uc_email_subscribers_user UNIQUE (user_id);