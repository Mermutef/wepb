CREATE TYPE post_status AS ENUM (
    'PUBLISHED',
    'HIDDEN',
    'MODERATION',
    'DRAFT'
);

DROP TABLE IF EXISTS hashtags;

CREATE TABLE hashtags (
	id SERIAL PRIMARY KEY,
	title VARCHAR(50) UNIQUE NOT NULL
);

DROP TABLE IF EXISTS posts;

CREATE TABLE posts (
	id SERIAL PRIMARY KEY,
	title VARCHAR(100) NOT NULL,
	preview VARCHAR(256) NOT NULL REFERENCES media(filename),
	content TEXT NOT NULL,
	hashtag INT NOT NULL REFERENCES hashtags(id),
	event_date TIMESTAMP WITH TIME ZONE,
	creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
	last_modified_date TIMESTAMP WITH TIME ZONE NOT NULL,
	authorId INT NOT NULL REFERENCES users(id),
	moderatorId INT REFERENCES users(id),
	status post_status NOT NULL
);

CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    content VARCHAR(256) NOT NULL,
    authorId INT NOT NULL REFERENCES users(id),
    postId INT NOT NULL REFERENCES posts(id),
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_date TIMESTAMP WITH TIME ZONE NOT NULL,
    is_hidden BOOLEAN NOT NULL
);
