CREATE TYPE content_type AS ENUM (
    'IMAGE',
    'SOUND',
    'VIDEO'
);

DROP TABLE IF EXISTS media;

CREATE TABLE media (
	filename VARCHAR(256) PRIMARY KEY,
	authorId INT NOT NULL REFERENCES users(id),
	birth_date TIMESTAMP NOT NULL,
    media_type content_type NOT NULL,
    content BYTEA NOT NULL,
    is_temporary BOOLEAN NOT NULL
);
