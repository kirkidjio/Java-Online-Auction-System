create table users (
	id uuid primary key,
	username varchar(100) not null,
	email varchar(100) not null,
	password varchar(250) not null,
	role varchar(30) not null default 'ROLE_USER'

);