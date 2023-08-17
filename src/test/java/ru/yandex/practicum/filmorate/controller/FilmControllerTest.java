package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private static final FilmController filmController = new FilmController(new FilmRepository());
    private static final String MESS_FILM_VALIDATE_EXCEPTION = "Неверно указаны имя, описание или дата релиза.";

    @BeforeAll
    public static void addFilm() throws ValidationException {
        Film film = new Film();
        film.setName("Человек-паук");
        film.setDescription("Питера Паркера укусил радиоактивный паук!");
        film.setDuration(121);
        film.setReleaseDate(LocalDate.of(2002, 4, 30));
        filmController.create(film);
    }

    @Test
    public void addFilmTest() {
        List<Film> films = filmController.findAll();
        assertEquals(1, films.size());

        Film film = films.get(0);

        assertAll("Сравнение полей фильма.",
                () -> assertEquals(1, film.getId()),
                () -> assertEquals("Человек-паук", film.getName()),
                () -> assertEquals("Питера Паркера укусил радиоактивный паук!", film.getDescription()),
                () -> assertEquals(121, film.getDuration()),
                () -> assertEquals(LocalDate.of(2002, 4, 30), film.getReleaseDate()));
    }

    @Test
    public void addFilmNullTest() {
        addFilmValidateException(null);
    }

    @Test
    public void addFilmWithoutName() {
        Film film = new Film();
        film.setDescription("Чудом избежав гибели, Максимус становится гладиатором.");
        film.setDuration(155);
        film.setReleaseDate(LocalDate.of(2000, 5, 1));
        addFilmValidateException(film);

        film.setName("");
        addFilmValidateException(film);

        film.setName(" ");
        addFilmValidateException(film);
    }

    @Test
    public void addFilmWithLongDescription() {
        Film film = new Film();
        film.setName("Гладиатор");
        film.setDescription("Чудом избежав гибели, Максимус становится гладиатором. " +
                "Быстро снискав себе славу в кровавых поединках, он оказывается в знаменитом римском Колизее, " +
                "на арене которого он встретится в смертельной схватке со своим заклятым врагом");
        film.setDuration(155);
        film.setReleaseDate(LocalDate.of(2000, 5, 1));
        addFilmValidateException(film);
    }

    @Test
    public void addFilmOldDateReleaseTest() {
        Film film = new Film();
        film.setName("Очень старый фильм");
        film.setDescription("Очень старый и выдуманный фильм вышел в свет 20.06.1890.");
        film.setDuration(60);
        film.setReleaseDate(LocalDate.of(1890, 6, 20));
        addFilmValidateException(film);
    }

    @Test
    public void addFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Гладиатор");
        film.setDescription("Чудом избежав гибели, Максимус становится гладиатором.");
        film.setDuration(-155);
        film.setReleaseDate(LocalDate.of(2000, 5, 1));
        addFilmValidateException(film);
    }

    @Test
    public void updateFilmWrongIdTest() {
        Film film = new Film();
        film.setId(2);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        filmController.update(film);
                    }
                });

        assertEquals("Неверно указан id фильма!", exception.getMessage());
    }

    @Test
    public void updateFilmTest() throws ValidationException {
        Film film = filmController.findAll().get(0);

        film.setName("Человек-паук 2");
        film.setDescription("Тихоня Питер Паркер балансирует на грани двух своих жизней: супергероя Человека-паука и " +
                "обычного студента колледжа.");
        film.setDuration(127);
        film.setReleaseDate(LocalDate.of(2004, 11, 18));
        filmController.update(film);

        List<Film> films = filmController.findAll();
        assertEquals(1, films.size());

        Film film1 = films.get(0);

        assertAll("Сравнение полей фильма.",
                () -> assertEquals(1, film.getId()),
                () -> assertEquals("Человек-паук 2", film.getName()),
                () -> assertEquals("Тихоня Питер Паркер балансирует на грани двух своих жизней: " +
                        "супергероя Человека-паука и " +
                        "обычного студента колледжа.", film.getDescription()),
                () -> assertEquals(127, film.getDuration()),
                () -> assertEquals(LocalDate.of(2004, 11, 18), film.getReleaseDate()));
    }

    @Test
    public void updateFilmWithoutName() {
        Film film = filmController.findAll().get(0);
        film.setName("");
        updateFilmValidateException(film);
        film.setName(" ");
        updateFilmValidateException(film);
    }

    @Test void updateFilmWithLongDescription() {
        Film film = filmController.findAll().get(0);
        film.setDescription("Питера Паркера укусил радиоактивный паук! Так Питер становится настоящим супергероем " +
                "по имени Человек-паук, который помогает людям и борется с преступностью. Но там, где есть супергерой, " +
                "рано или поздно всегда объявляется и суперзлодей");
        updateFilmValidateException(film);
    }

    @Test void updateFilmWithNegativeTest() {
        Film film = filmController.findAll().get(0);
        film.setDuration(-121);
        updateFilmValidateException(film);
    }

    private void addFilmValidateException(Film film) {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        filmController.create(film);
                    }
                });

        if (film == null) {
            assertEquals("Фильм отсутствует!", exception.getMessage());
        } else {
            assertEquals(MESS_FILM_VALIDATE_EXCEPTION, exception.getMessage());
        }
    }

    private void updateFilmValidateException(Film film) {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        filmController.update(film);
                    }
                });

        assertEquals(MESS_FILM_VALIDATE_EXCEPTION, exception.getMessage());
    }
}
