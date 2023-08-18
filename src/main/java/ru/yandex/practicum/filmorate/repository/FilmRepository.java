package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FilmRepository {
    private int filmId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public boolean findFilmId(int id) {
        return films.containsKey(id);
    }

    public Film save(Film film) {
        film.setId(filmId);
        films.put(filmId++, film);
        return film;
    }

    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }
}
