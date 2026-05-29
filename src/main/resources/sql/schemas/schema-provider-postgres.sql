-- Table: public.ncmmis_provider

-- DROP TABLE IF EXISTS ncmmis_provider;

CREATE TABLE IF NOT EXISTS ncmmis_provider
(
    id integer NOT NULL,
    npi bigint NOT NULL,
    last_name character varying(255) COLLATE pg_catalog."default",
    first_name character varying(255) COLLATE pg_catalog."default",
    ssn character varying(255) COLLATE pg_catalog."default",
    email character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT mmis_provider_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ncmmis_provider
    OWNER to postgres;