package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    boolean findFilmId(int id);

    Film save(Film film);

    Film update(Film film);

    Film getFilmById(int id);

    List<Film> getPopularFilms(int count);

    List<Film> searchFilms(String query, List<String> by, int count);
}
