CREATE TABLE Contact
(
	"id" SERIAL PRIMARY KEY,
	"firstName" character varying(128),
    "lastName" character varying(128),
    email character varying(128),
    website character varying(128),
    "phoneNumber" character varying(128),
    address character varying(128),
    city character varying(128),
    state character varying(128),
    country character varying(128)
);
