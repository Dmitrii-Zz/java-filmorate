DROP TABLE IF EXISTS users, films, rating, genre_film, genres, friends, likes CASCADE;

CREATE TABLE IF NOT EXISTS users (
	user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR,
	email VARCHAR NOT NULL,
	login VARCHAR NOT NULL,
	birthday date
);

ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS uq_email UNIQUE (email);
ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS uq_login UNIQUE (login);

CREATE TABLE IF NOT EXISTS rating (
	rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS directors (
	director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
	film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL,
	description VARCHAR(200),
	release_date date,
	duration INTEGER,
	rating_id INTEGER REFERENCES rating (rating_id)
);

CREATE TABLE IF NOT EXISTS director_film (
	film_id INTEGER REFERENCES films (film_id),
	director_id INTEGER REFERENCES directors (director_id)
);

CREATE TABLE IF NOT EXISTS likes (
	film_id INTEGER REFERENCES films (film_id),
	user_id INTEGER REFERENCES users (user_id),
	PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS genres (
	genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar
);

CREATE TABLE IF NOT EXISTS genre_film (
	film_id INTEGER REFERENCES films (film_id),
	genre_id INTEGER REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS friends (
	user_id INTEGER REFERENCES users (user_id),
	friend_id INTEGER REFERENCES users (user_id),
	friendship_status BOOLEAN DEFAULT 'false',
	PRIMARY KEY (user_id, friend_id)
);