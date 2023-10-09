package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    List<Genre> findAllGenres();

    Genre findGenreById(int id);

    Set<Genre> findGenreByFilmId(int id);

    void saveGenreFilm(int filmId, int genreId);

    void deleteGenreFilm(int filmID);
}
