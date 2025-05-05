DROP TABLE IF EXISTS directions;

CREATE TABLE directions (
	id SERIAL PRIMARY KEY,
	name CHARACTER(255) UNIQUE NOT NULL,
	description TEXT NOT NULL,
	logo_path VARCHAR(256) NOT NULL REFERENCES media(filename),
	banner_path VARCHAR(256) NOT NULL REFERENCES media(filename),
	chairman_id INT NOT NULL REFERENCES users(id),
	deputy_сhairman_id INT NOT NULL REFERENCES users(id)
);
