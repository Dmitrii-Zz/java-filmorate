package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeFilmsStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.Constants.*;
import static ru.yandex.practicum.filmorate.Constants.MIN_DURATION_FILM;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmRepository;
    private final UserStorage userRepository;
    private final LikeFilmsStorage likeFilmRepository;
    private final DirectorStorage directorRepository;

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
        filmRepository.getFilmById(filmId).setLikes(createListLikes(filmId, userId));
        likeFilmRepository.addLike(filmId, userId);
    }


    public void deleteLike(int filmId, int userId) {
        validateIdFilm(filmId);
        validateIdUser(userId);
        likeFilmRepository.deleteLike(filmId, userId);
    }

    public List<Film> popularFilms(int count) {

        if (count < CORRECT_COUNT) {
            throw new FilmValidationException(String.format("Передан неверный параметр count = \"%d\"", count));
        }

        return filmRepository.getPopularFilms(count);
    }

    public List<Film> getFilmsByDirector(int id, String sortBy) {

        if (!directorRepository.findDirectorById(id)) {
            throw new DirectorNotFoundException(String.format("Режиссер с id = %d отсутствует", id));
        }

        return filmRepository.getFilmsByDirector(id, sortBy);
    }

    private Set<Integer> createListLikes(int filmId, int userId) {
        Set<Integer> likes = filmRepository.getFilmById(filmId).getLikes();
        if (likes == null) {
            likes = new HashSet<>();
        }

        likes.add(userId);
        return likes;
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
