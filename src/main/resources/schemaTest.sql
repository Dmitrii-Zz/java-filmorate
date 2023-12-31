DROP TABLE IF EXISTS rating, directors, genres, films, director_film, genre_film, users, likes, friends, reviews, reviews_likes CASCADE;

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

CREATE TABLE IF NOT EXISTS films (
	film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL,
	description VARCHAR(200),
	release_date date,
	duration INTEGER,
	rating_id INTEGER REFERENCES rating (rating_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes (
	film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
	user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
	PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS genres (
	genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR
);

CREATE TABLE IF NOT EXISTS genre_film (
	film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
	genre_id INTEGER REFERENCES genres (genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friends (
	user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
	friend_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
	friendship_status BOOLEAN DEFAULT 'false',
	PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS directors (
    director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS director_film (
    film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    director_id INTEGER REFERENCES directors (director_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS reviews (
	review_id INTEGER primary key auto_increment,
	review_content varchar(255),
	is_positive boolean default 'true',
	user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
	film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    useful INTEGER default 0
);

CREATE TABLE IF NOT EXISTS reviews_likes (
	review_like_id INTEGER primary key auto_increment,
	review_id INTEGER REFERENCES reviews (review_id) ON DELETE CASCADE,
	user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
	is_positive boolean default 'true'
);