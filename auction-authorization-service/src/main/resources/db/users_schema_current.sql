CREATE TABLE users.users (
    id uuid NOT NULL,
    username character varying(100) NOT NULL,
    email character varying(100) NOT NULL,
    password character varying(250) NOT NULL,
    role character varying(30) DEFAULT 'ROLE_USER'::character varying NOT NULL
);
ALTER TABLE ONLY users.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);
