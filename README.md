# java-filmorate
Template repository for Filmorate project.

База данных приложения имеет следующую структуру:

<image src="/src/main/resources/BD.jpg" alt="Структура базы данных">

Вот несколько запросов для работы с базой данных.

__Отобразить всех пользователей:__

SELECT (*)  
FROM user

__Отобразить все фильмы:__

SELECT (*)  
FROM film

__Отобразить имена пользователей, которые лайкнули фильм "Платформа":__

SELECT name  
FROM user  
WHERE user_id = (SELECT likes.user_id  
FROM likes  
WHERE likes.film.id = (SELECT film.film_id  
FROM film  
WHERE film.name = 'Платформа'));

__Отобразить существующие жанры фильмов:__

SELECT name  
FROM ganre

