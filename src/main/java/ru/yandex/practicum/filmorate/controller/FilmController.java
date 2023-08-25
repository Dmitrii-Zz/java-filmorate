package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import static ru.yandex.practicum.filmorate.Constants.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;
    private final FilmService filmService;
    @GetMapping
    public List<Film> findAll() {
        return inMemoryFilmStorage.findAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {

        if (id < CORRECT_ID) {
            throw new FilmValidationException(String.format("Передан неверный id = \"%d\" ", id));
        }

        if (!inMemoryFilmStorage.findFilmId(id)) {
            throw new FilmNotFoundException(String.format("Фильм с \"%d\" отсутствует", id));
        }

        return inMemoryFilmStorage.getFilmById(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        if (film == null) {
            throw new FilmNotFoundException("Фильм отсутствует!");
        }

        validate(film);
        return inMemoryFilmStorage.save(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {

        if (!inMemoryFilmStorage.findFilmId(film.getId())) {
            throw new FilmNotFoundException("Неверно указан id фильма.");
        }

        validate(film);
        return inMemoryFilmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable int id, @PathVariable int userId) {
        validateId(id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        validateId(id, userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> popularFilms(@RequestParam(defaultValue = "10") int count) {

        if (count < CORRECT_COUNT) {
            throw new FilmValidationException(String.format("Передан неверный параметр count = \"%d\" ", count));
        }

        return filmService.popularFilms(count);
    }

    private void validateId(int filmId, int userId) {
        if (filmId < CORRECT_ID) {
            throw new FilmValidationException(String.format("Передан некорректный ИД фильма - id = \"%d\" ", filmId));
        }

        if (userId < CORRECT_ID) {
            throw new UserNotFoundException(
                    String.format("Передан некорректный ИД пользователя - id = \"%d\" ", userId));
        }

        if (!inMemoryFilmStorage.findFilmId(filmId)) {
            throw new FilmNotFoundException(String.format("Фильм с id = \"%d\" отсутствует", filmId));
        }

        if (!inMemoryUserStorage.findUserId(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id = \"%d\" отсутствует", userId));
        }
    }

    private void validate(Film film) {

        if (film.getName() == null) {
            throw new FilmValidationException("В запросе отсутствует название фильма - name - null");
        }

        if (film.getName().isBlank()) {
            throw new FilmValidationException("Название фильма не должно быть пустым.");
        }

        if (!film.getReleaseDate().isAfter(VALIDATE_DATE_FILM)) {
            throw new FilmValidationException("Неверная дата релиза.");
        }

        if (!(film.getDescription().length() <= MAX_LENGTH_DESCRIPTION)) {
            throw new FilmValidationException("Описание фильма не должно превышать 200 символов.");
        }

        if (!(film.getDuration() > MIN_DURATION_FILM)) {
            throw new FilmValidationException("Неверная продолжительность фильма.");
        }
    }
}