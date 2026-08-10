CREATE TABLE notifications.email_subscribers (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    email character varying(255) NOT NULL,
    is_subscribed boolean NOT NULL,
    username character varying(255) NOT NULL
);
ALTER TABLE ONLY notifications.email_subscribers
    ADD CONSTRAINT pk_email_subscribers PRIMARY KEY (id);
ALTER TABLE ONLY notifications.email_subscribers
    ADD CONSTRAINT uc_email_subscribers_email UNIQUE (email);
ALTER TABLE ONLY notifications.email_subscribers
    ADD CONSTRAINT uc_email_subscribers_user UNIQUE (user_id);
ALTER TABLE ONLY notifications.email_subscribers
    ADD CONSTRAINT uc_email_subscribers_username UNIQUE (username);
