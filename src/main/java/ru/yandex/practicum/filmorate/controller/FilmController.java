package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmRepository filmRepository = new FilmRepository();
    private static final int MIN_DURATION_FILM = 0;
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate VALIDATE_DATE_FILM = LocalDate.of(1895, 12, 28);

    @GetMapping
    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        if (film == null) {
            log.debug("Фильм отсутствует!");
            throw new ValidationException("Фильм отсутствует!");
        }

        if (!validate(film)) {
            log.debug("Валидация при создании фильма не пройдена!");
            throw new ValidationException("Неверно указаны имя, описание или дата релиза.");
        }

        return filmRepository.save(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {

        if (!filmRepository.findFilmId(film.getId())) {
            log.debug("Фильм с id '{}' отсутствует", film.getId());
            throw new ValidationException("Неверно указан id фильма!");
        }

        if (!validate(film)) {
            log.debug("Валидация при обновления фильма не пройдена!");
            throw new ValidationException("Неверно указаны имя, описание или дата релиза.");
        }

        return filmRepository.update(film);
    }

    private boolean validate(Film film) {

        if (film.getName() == null) {
            return false;
        }

        return !film.getName().isBlank()
               && film.getReleaseDate().isAfter(VALIDATE_DATE_FILM)
               && film.getDescription().length() <= MAX_LENGTH_DESCRIPTION
               && film.getDuration() > MIN_DURATION_FILM;
    }
}