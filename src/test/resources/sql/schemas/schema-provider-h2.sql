CREATE TABLE IF NOT EXISTS ncmmis_provider
(
    id integer NOT NULL,
    npi bigint NOT NULL,
    last_name varchar(255),
    first_name varchar(255),
    ssn varchar(255),
    email varchar(255),
    CONSTRAINT mmis_provider_pkey PRIMARY KEY (id)
);
