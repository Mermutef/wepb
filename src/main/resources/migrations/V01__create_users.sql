CREATE TYPE user_role AS ENUM (
    'ADMIN',
    'MODERATOR',
    'WRITER',
    'READER'
);

DROP TABLE IF EXISTS users;

CREATE TABLE users (
	id SERIAL PRIMARY KEY,
	name CHARACTER(64) NOT NULL,
	surname CHARACTER(64) NOT NULL,
	login CHARACTER VARYING(30) UNIQUE NOT NULL,
	email VARCHAR(255) UNIQUE NOT NULL,
	phoneNumber VARCHAR(11) NOT NULL,
    password CHARACTER(64) NOT NULL,
    vkLink VARCHAR(255),
    role user_role NOT NULL
);
