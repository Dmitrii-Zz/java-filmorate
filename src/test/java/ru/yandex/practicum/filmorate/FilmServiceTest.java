package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {

    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private final FilmController filmController =
            new FilmController(new FilmService(new InMemoryFilmStorage(), inMemoryUserStorage));
    private final UserController userController =
            new UserController(new UserService(inMemoryUserStorage));

    @BeforeEach
    public void addFilm() {
        Film film = Film.builder().build();
        film.setName("Человек-паук");
        film.setDescription("Питера Паркера укусил радиоактивный паук!");
        film.setDuration(121);
        film.setReleaseDate(LocalDate.of(2002, 4, 30));
        filmController.createFilm(film);

        Film film1 = Film.builder().build();
        film1.setName("Платформа");
        film1.setDescription("Горен соглашается на участие в некоем эксперименте и вскоре приходит в себя в почти");
        film1.setDuration(94);
        film1.setReleaseDate(LocalDate.of(2019, 11, 8));
        filmController.createFilm(film1);

        User user = User.builder().build();
        user.setName("Михалыч");
        user.setEmail("Mihail@yandex.ru");
        user.setLogin("Misha");
        user.setBirthday(LocalDate.of(1997, 4, 1));
        userController.createUser(user);
    }

    @Test
    public void addFilmTest() {
        List<Film> films = filmController.findAllFilms();
        assertEquals(2, films.size());

        Film film = films.get(0);

        assertAll("Сравнение полей фильма.",
                () -> assertEquals(1, film.getId()),
                () -> assertEquals("Человек-паук", film.getName()),
                () -> assertEquals("Питера Паркера укусил радиоактивный паук!", film.getDescription()),
                () -> assertEquals(121, film.getDuration()),
                () -> assertEquals(LocalDate.of(2002, 4, 30), film.getReleaseDate()),
                () -> assertEquals(0, film.getLikes().size()));
    }


    @Test
    public void addFilmNullTest() {
        final FilmNotFoundException exception = assertThrows(
                FilmNotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.createFilm(null);
                    }
                });

        assertEquals("В запросе отсутствует фильм.", exception.getMessage());
    }

    @Test
    public void addFilmWithoutName() {
        Film film = filmController.getFilm(1);
        film.setName(null);

        final FilmValidationException exception = assertThrows(
                FilmValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.createFilm(film);
                    }
                });

        assertEquals("В запросе отсутствует название фильма - name - null", exception.getMessage());

        film.setName("");

        final FilmValidationException exception1 = assertThrows(
                FilmValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.createFilm(film);
                    }
                });

        assertEquals("Название фильма не должно быть пустым.", exception1.getMessage());

        film.setName(" ");

    }


    @Test
    public void addFilmWithLongDescription() {
        Film film = Film.builder().build();
        film.setName("Гладиатор");
        film.setDescription("Чудом избежав гибели, Максимус становится гладиатором. " +
                "Быстро снискав себе славу в кровавых поединках, он оказывается в знаменитом римском Колизее, " +
                "на арене которого он встретится в смертельной схватке со своим заклятым врагом");
        film.setDuration(155);
        film.setReleaseDate(LocalDate.of(2000, 5, 1));

        final FilmValidationException exception1 = assertThrows(
                FilmValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.createFilm(film);
                    }
                });

        assertEquals("Описание фильма не должно превышать 200 символов.", exception1.getMessage());

    }

    @Test
    public void addFilmOldDateReleaseTest() {
        Film film = Film.builder().build();
        film.setName("Очень старый фильм");
        film.setDescription("Очень старый и выдуманный фильм вышел в свет 20.06.1890.");
        film.setDuration(60);
        film.setReleaseDate(LocalDate.of(1890, 6, 20));

        final FilmValidationException exception1 = assertThrows(
                FilmValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.createFilm(film);
                    }
                });

        assertEquals("Некорректная дата релиза фильма.", exception1.getMessage());
    }

    @Test
    public void addFilmWithNegativeDuration() {
        Film film = filmController.getFilm(1);
        film.setDuration(-155);

        final FilmValidationException exception1 = assertThrows(
                FilmValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.createFilm(film);
                    }
                });

        assertEquals("Продолжительность фильма не может быть отрицательной.", exception1.getMessage());
    }

    @Test
    public void updateFilmWrongIdTest() {
        Film film = filmController.getFilm(1);
        film.setId(33);
        final FilmNotFoundException exception = assertThrows(
                FilmNotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.updateFilm(film);
                    }
                });

        assertEquals("Фильм с id = \"33\" отсутствует", exception.getMessage());

        film.setId(-33);

        final FilmValidationException exception2 = assertThrows(
                FilmValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.updateFilm(film);
                    }
                });

        assertEquals("Передан некорректный ИД фильма - id = \"-33\"", exception2.getMessage());
    }



    @Test
    public void updateFilmTest() {
        Film film = filmController.getFilm(1);

        film.setName("Человек-паук 2");
        film.setDescription("Тихоня Питер Паркер балансирует на грани двух своих жизней: супергероя Человека-паука и " +
                "обычного студента колледжа.");
        film.setDuration(127);
        film.setReleaseDate(LocalDate.of(2004, 11, 18));
        filmController.updateFilm(film);

        List<Film> films = filmController.findAllFilms();
        assertEquals(2, films.size());

        Film film1 = films.get(0);

        assertAll("Сравнение полей фильма.",
                () -> assertEquals(1, film.getId()),
                () -> assertEquals("Человек-паук 2", film.getName()),
                () -> assertEquals("Тихоня Питер Паркер балансирует на грани двух своих жизней: " +
                        "супергероя Человека-паука и " +
                        "обычного студента колледжа.", film.getDescription()),
                () -> assertEquals(127, film.getDuration()),
                () -> assertEquals(LocalDate.of(2004, 11, 18), film.getReleaseDate()),
                () -> assertEquals(0, film1.getLikes().size()));
    }

    @Test
    public void addLikeFilmTest() {
        filmController.addLikeFilm(1, 1);
        assertEquals(1, filmController.getFilm(1).getLikes().size());
    }

    @Test void addLikeFilmWrongIdUserTest() {
        final UserNotFoundException exception2 = assertThrows(
                UserNotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.addLikeFilm(1, 22);
                    }
                });

        assertEquals("Пользователь с id = \"22\" отсутствует", exception2.getMessage());

        final UserNotFoundException exception3 = assertThrows(
                UserNotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.addLikeFilm(1, -22);
                    }
                });

        assertEquals("Передан некорректный ИД пользователя - id = \"-22\"", exception3.getMessage());
    }

    @Test
    public void getPopularFilmsWrongCountTest() {
        final FilmValidationException exception = assertThrows(
                FilmValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.popularFilms(-1);
                    }
                });

        assertEquals("Передан неверный параметр count = \"-1\"", exception.getMessage());
    }

    @Test
    public void getPopularFilmsTest() {
        filmController.addLikeFilm(1, 1);
        List<Film> popularFilms = filmController.popularFilms(2);
        assertEquals(2, popularFilms.size());
        assertEquals(1, popularFilms.get(0).getLikes().size());
        assertEquals(0, popularFilms.get(1).getLikes().size());
    }

    @Test
    public void deleteLikeFilmTest() {
        filmController.addLikeFilm(1, 1);
        assertEquals(1, filmController.getFilm(1).getLikes().size());
        filmController.deleteLikeFilm(1, 1);
        assertEquals(0, filmController.getFilm(1).getLikes().size());
    }
}
