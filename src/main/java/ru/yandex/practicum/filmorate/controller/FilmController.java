package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private static final LocalDate VALIDATE_DATE_FILM = LocalDate.of(1895, 12, 28);
    private boolean isValidate;
    private int filmId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {

        if (film == null) {
            log.debug("Фильм отсутствует!");
            throw new ValidationException("Фильм отсутствует!");
        }

        isValidate = filmNameValidate(film)
                && film.getReleaseDate().isAfter(VALIDATE_DATE_FILM)
                && film.getDescription().length() <= 200
                && film.getDuration() > 0;

        if (isValidate) {
            film.setId(filmId);
            films.put(filmId, film);
            filmId++;
        } else {
            log.debug("Валидация при создании фильма не пройдена!");
            throw new ValidationException("Неверно указаны имя, описание или дата релиза.");
        }

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) throws ValidationException {

        if (!films.containsKey(film.getId())) {
            log.debug("Фильм с id '{}' отсутствует", film.getId());
            throw new ValidationException("Неверно указан id фильма!");
        }

        isValidate = filmNameValidate(film)
                && film.getReleaseDate().isAfter(VALIDATE_DATE_FILM)
                && film.getDescription().length() <= 200
                && film.getDuration() > 0;

        if (isValidate) {
            films.put(film.getId(), film);
        } else {
            log.debug("Валидация при обновления фильма не пройдена!");
            throw new ValidationException("Неверно указаны имя, описание или дата релиза.");
        }

        return film;
    }

    private boolean filmNameValidate(Film film) {
        if (film.getName() == null) return false;
        return !film.getName().isBlank();
    }
}
