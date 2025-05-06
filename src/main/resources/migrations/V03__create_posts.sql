CREATE TYPE post_status AS ENUM (
    'PUBLISHED',
    'HIDDEN',
    'MODERATION',
    'DRAFT'
);

DROP TABLE IF EXISTS hashtags;

CREATE TABLE hashtags (
	id SERIAL PRIMARY KEY,
	title VARCHAR(50) NOT NULL
);

DROP TABLE IF EXISTS posts;

CREATE TABLE posts (
	id SERIAL PRIMARY KEY,
	title VARCHAR(100) NOT NULL,
	preview VARCHAR(256) NOT NULL REFERENCES media(filename),
	content TEXT NOT NULL,
	hashtag INT NOT NULL REFERENCES hashtags(id),
	event_date TIMESTAMP,
	creation_date TIMESTAMP NOT NULL,
	last_modified_date TIMESTAMP NOT NULL,
	authorId INT NOT NULL REFERENCES users(id),
	moderatorId INT REFERENCES users(id),
	status post_status NOT NULL
);
--	дата публикации ZonedDateTime.now()
--  Менять время вручную
-- дата публикации (если опубликован) и дата создания (если не опубликован)
--Селекты:
--- по id поста
--- все постыв с конкретным хэштегом
--- N самых новых постов
--- все посты
--- все посты по автору
--- все посты по статусу
--- все посты по модератору
--- все посты по интервалу даты публикации (опубликованы/созданы в промежутке с такой то даты до такой то, конечная дата может быть не задана)
--
--Апдейты:
--- title, text_body, event_date, preview в одном запросе
--- status
--- moderatorId
--- hashtag
--
--отдельный метод
--- есть ли посты с данным хэштегом



--CREATE OR REPLACE FUNCTION update_last_modified_date()
--RETURNS TRIGGER AS $$
--BEGIN
--    NEW.last_modified_date = CURRENT_TIMESTAMP;
--    RETURN NEW;
--END;
--$$ LANGUAGE plpgsql;
--
--CREATE TRIGGER update_posts_modtime
--BEFORE UPDATE ON posts
--FOR EACH ROW
--EXECUTE FUNCTION update_last_modified_date();


--event_date TIMESTAMP WITH TIME ZONE, -- @javaType java.time.ZonedDateTime
--	creation_date TIMESTAMP WITH TIME ZONE NOT NULL, -- @javaType java.time.ZonedDateTime
--	last_modified_date TIMESTAMP WITH TIME ZONE NOT NULL, -- @javaType java.time.ZonedDateTime