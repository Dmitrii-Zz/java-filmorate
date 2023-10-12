INSERT INTO genres (name) VALUES
	('Комедия'),
	('Драма'),
	('Мультфильм'),
	('Триллер'),
	('Документальный'),
	('Боевик');

INSERT INTO rating (name) VALUES
	('G'),
	('PG'),
	('PG-13'),
	('R'),
	('NC-17');

--INSERT INTO directors (name) VALUES
--('Jack London'),
--('Brush Brush'),
--('White Wolfgang');


--INSERT INTO films (film_id, name, description, release_date, duration, rating_id) values (1, 'white fang', 'description1', '2002-12-03', 120, 4);
--INSERT INTO films (film_id, name, description, release_date, duration, rating_id) values (3, 'Old tree', 'description3', '2010-12-26', 182, 1);
--INSERT INTO films (film_id, name, description, release_date, duration, rating_id) values (2, 'Java for white teapot', 'description1', '2000-12-03', 140, 2);
--INSERT INTO users (user_id, email, login, name, birthday) values (1, 'greip@mail.ru', 'greip', 'kirill', '1983-01-03');
--INSERT INTO users (user_id, email, login, name, birthday) values (3, 'grey@mail.ru', 'grey', 'Oleg', '1989-11-03');
--INSERT INTO users (user_id, email, login, name, birthday) values (2, 'kitty@mail.ru', 'pussssy', 'mady', '1990-06-20');
--
--INSERT INTO likes (film_id, user_id) values (2, 1);
--INSERT INTO likes (film_id, user_id) values (2, 2);
--INSERT INTO likes (film_id, user_id) values (1, 1);
--INSERT INTO likes (film_id, user_id) values (2, 3);
--
--INSERT INTO director_film (director_id, film_id) values (1, 1);
--INSERT INTO director_film (director_id, film_id) values (1, 2);
--INSERT INTO director_film (director_id, film_id) values (2, 2);
--INSERT INTO director_film (director_id, film_id) values (1, 3);
--
--INSERT INTO genre_film (genre_id, film_id) values (2, 2);
--INSERT INTO genre_film (genre_id, film_id) values (5, 1);
--INSERT INTO genre_film (genre_id, film_id) values (6, 3);