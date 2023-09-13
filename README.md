# java-filmorate

Бэкенд для сервиса, который будет работать с фильмами и оценками пользователей, а также возвращать топ-5 фильмов, 
рекомендованных к просмотру. Теперь ни вам, ни вашим друзьям не придётся долго размышлять, что посмотреть вечером.

База данных приложения имеет следующую структуру:

<image src="/src/main/resources/BD4.jpg" alt="Структура базы данных">

Вот несколько запросов для работы с базой данных.

__1. Отобразить всех пользователей:__

```
SELECT (*)
FROM users;
```

__2. Отобразить названия фильмов жанра "Ужас":__

```
SELECT films.name
FROM films
WHERE films.film_id = (SELECT genre_film.film_id
                 FROM genre_film
                 WHERE genre_id = (SELECT genre_id
                                   FROM genres
                                   WHERE name = 'Ужас')) AS film_horror;               
```
__3. Отобразить все фильмы:__

```
SELECT (*)  
FROM films;
```

__4. Отобразить существующие жанры фильмов:__

```
SELECT name  
FROM genres
```

