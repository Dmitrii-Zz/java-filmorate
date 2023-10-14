package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    boolean findFilmId(int id);

    Film save(Film film);

    Film update(Film film);

    Film getFilmById(int id);

    void deleteFilmById(int filmId);

    List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    List<Film> getPopularFilms(int count);

    List<Film> getFilmsByDirector(int id, String sortBy);

    List<Film> searchFilms(String query, List<String> by);

  //  List<Film> searchFilms(String query, List<String> by, int count);
}
