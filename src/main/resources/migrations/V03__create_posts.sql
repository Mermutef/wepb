CREATE TYPE post_status AS ENUM (
    'PUBLISHED',
    'HIDDEN',
    'MODERATION',
    'DRAFT'
);

DROP TABLE IF EXISTS posts;

CREATE TABLE posts (
	id SERIAL PRIMARY KEY,
	title VARCHAR(100) NOT NULL,
	preview VARCHAR(256) NOT NULL REFERENCES media(filename),
	text_body VARCHAR(2048) NOT NULL,
	event_date TIMESTAMP,
	creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	authorId INT NOT NULL REFERENCES users(id),
	moderatorId INT NOT NULL REFERENCES users(id),
	status post_status NOT NULL
);

CREATE OR REPLACE FUNCTION update_last_modified_date()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_modified_date = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_posts_modtime
BEFORE UPDATE ON posts
FOR EACH ROW
EXECUTE FUNCTION update_last_modified_date();



DROP TABLE IF EXISTS hashtag;

CREATE TABLE hashtag (
	id SERIAL PRIMARY KEY,
	title VARCHAR(100) NOT NULL
);



DROP TABLE IF EXISTS post_to_hashtag;

CREATE TABLE post_to_hashtag (
	id SERIAL PRIMARY KEY,
	postId INT NOT NULL REFERENCES posts(id),
	hashtagId INT NOT NULL REFERENCES hashtag(id)
);



DROP TABLE IF EXISTS post_to_media;

CREATE TABLE post_to_media (
	id SERIAL PRIMARY KEY,
	postId INT NOT NULL REFERENCES posts(id),
	media_name VARCHAR(256) NOT NULL REFERENCES media(filename)
);



