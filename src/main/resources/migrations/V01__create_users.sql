CREATE TYPE user_role AS ENUM (
    'ADMIN',
    'MODERATOR',
    'AUTHORIZED'
);

DROP TABLE IF EXISTS users;

CREATE TABLE users (
	id SERIAL PRIMARY KEY,
	name CHARACTER VARYING(30) UNIQUE NOT NULL,
	email VARCHAR(255) UNIQUE NOT NULL,
    password CHARACTER(64) NOT NULL,
    role user_role NOT NULL,
    CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);
