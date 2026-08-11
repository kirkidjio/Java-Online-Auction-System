CREATE TABLE lots.bids (
    id uuid NOT NULL,
    buyer_id uuid NOT NULL,
    currency character varying(3) NOT NULL,
    value numeric(15,2),
    lot_id uuid,
    CONSTRAINT bids_value_check CHECK ((value > (0)::numeric))
);
CREATE TABLE lots.lots (
    id uuid NOT NULL,
    owner_id uuid,
    timeout timestamp without time zone,
    description text,
    created_at timestamp without time zone DEFAULT now(),
    min_bid numeric(15,2),
    currency character varying(3),
    status text,
    title character varying(250),
    CONSTRAINT lots_min_bid_check CHECK ((min_bid > (0)::numeric)),
    CONSTRAINT lots_status_check CHECK ((status = ANY (ARRAY['OPEN'::text, 'CLOSED'::text, 'DRAW'::text])))
);
ALTER TABLE ONLY lots.bids
    ADD CONSTRAINT bids_pkey PRIMARY KEY (id);
ALTER TABLE ONLY lots.lots
    ADD CONSTRAINT lots_pkey PRIMARY KEY (id);
ALTER TABLE ONLY lots.bids
    ADD CONSTRAINT bids_lot_id_fkey FOREIGN KEY (lot_id) REFERENCES lots.lots(id);
