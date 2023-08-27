package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int filmId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean findFilmId(int id) {
        return films.containsKey(id);
    }

    @Override
    public Film save(Film film) {
        film.setId(filmId);
        film.setLikes(new HashSet<>());
        films.put(filmId++, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        film.setLikes(films.get(film.getId()).getLikes());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        return films.get(id);
    }
}
