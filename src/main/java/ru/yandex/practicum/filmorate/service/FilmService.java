package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.Constants.*;
import static ru.yandex.practicum.filmorate.Constants.MIN_DURATION_FILM;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmRepository;
    private final UserStorage userRepository;

    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmRepository.save(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        validateIdFilm(film.getId());
        return filmRepository.update(film);
    }

    public Film getFilm(int id) {
        validateIdFilm(id);
        return filmRepository.getFilmById(id);
    }

    public void addLike(int filmId, int userId) {
        validateIdFilm(filmId);
        validateIdUser(userId);
        Film film = filmRepository.getFilmById(filmId);
        film.getLikes().add(userId);
    }

    public void deleteLike(int filmId, int userId) {
        validateIdFilm(filmId);
        validateIdUser(userId);
        Film film = filmRepository.getFilmById(filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> popularFilms(int count) {

        if (count < CORRECT_COUNT) {
            throw new FilmValidationException(String.format("Передан неверный параметр count = \"%d\"", count));
        }

        return filmRepository.findAll().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateIdFilm(int id) {
        if (id < CORRECT_ID) {
            throw new FilmValidationException(String.format("Передан некорректный ИД фильма - id = \"%d\"", id));
        }

        if (!filmRepository.findFilmId(id)) {
            throw new FilmNotFoundException(String.format("Фильм с id = \"%d\" отсутствует", id));
        }
    }

    private void validateIdUser(int id) {
        if (id < CORRECT_ID) {
            throw new UserNotFoundException(
                    String.format("Передан некорректный ИД пользователя - id = \"%d\"", id));
        }

        if (!userRepository.findUserId(id)) {
            throw new UserNotFoundException(String.format("Пользователь с id = \"%d\" отсутствует", id));
        }
    }

    private void validateFilm(Film film) {

        if (film == null) {
            throw new FilmNotFoundException("В запросе отсутствует фильм.");
        }

        if (film.getName() == null) {
            throw new FilmValidationException("В запросе отсутствует название фильма - name - null");
        }

        if (film.getName().isBlank()) {
            throw new FilmValidationException("Название фильма не должно быть пустым.");
        }

        if (!film.getReleaseDate().isAfter(VALIDATE_DATE_FILM)) {
            throw new FilmValidationException("Некорректная дата релиза фильма.");
        }

        if (!(film.getDescription().length() <= MAX_LENGTH_DESCRIPTION)) {
            throw new FilmValidationException("Описание фильма не должно превышать 200 символов.");
        }

        if (film.getDuration() <= MIN_DURATION_FILM) {
            throw new FilmValidationException("Продолжительность фильма не может быть отрицательной.");
        }
    }
}
