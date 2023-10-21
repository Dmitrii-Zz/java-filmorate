package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {

    List<Director> findAll();

    Director getDirectorById(int id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);

    boolean findDirectorById(int id);

    List<Director> findDirectorFilm(int filmId);
}
