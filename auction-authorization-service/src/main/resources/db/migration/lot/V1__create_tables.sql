create table lots (
	id uuid primary key,
	owner_id uuid,
	timeout timestamp ,
	description text,
	created_at timestamp,
	min_bid numeric(15,2) check (min_bid > 0),
	currency varchar(3),
	status text check(status in ('OPEN', 'CLOSED', 'DRAW')),
	title varchar(250)
);

create table bids (
	id uuid primary key,
	buyer_id uuid not null,
	currency varchar(3) not null,
	value numeric(15,2) check (value > 0),
	lot_id uuid references lots(id)

);