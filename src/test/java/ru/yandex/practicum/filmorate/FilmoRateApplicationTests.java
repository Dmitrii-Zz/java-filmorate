
package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.controller.RatingController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmoRateApplicationTests {
    private final UserController userController;
    private final FilmController filmController;
    private final GenreController genreController;
    private final RatingController ratingController;

    @Test
    public void filmoRateTest() {
        log.info("Тест создания первого пользователя:");
        User user = User.builder().build();
        user.setName("Николай");
        user.setEmail("Nikolay@yandex.ru");
        user.setLogin("Nikolay1995");
        user.setBirthday(LocalDate.of(1995, 5, 15));
        User saveUser = userController.createUser(user);

        assertAll("Проверка пользователя",
                () -> assertEquals(1, saveUser.getId()),
                () -> assertEquals("Николай", saveUser.getName()),
                () -> assertEquals("Nikolay@yandex.ru", saveUser.getEmail()),
                () -> assertEquals("Nikolay1995", saveUser.getLogin()),
                () -> assertEquals(LocalDate.of(1995, 5, 15), saveUser.getBirthday()),
                () -> assertEquals(0, saveUser.getFriends().size())
        );

        log.info("Тест создания второго пользователя:");
        User user2 = User.builder().build();
        user2.setName("Дмитрий");
        user2.setEmail("Dmitriy@yandex.ru");
        user2.setLogin("Dmitriy3333");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        User saveUser2 = userController.createUser(user2);

        assertEquals(2, saveUser2.getId());

        log.info("Тест метода запроса пользователя по ИД");
        User getUser = userController.getUser(1);
        assertAll("Проверка пользователя",
                () -> assertEquals(1, getUser.getId()),
                () -> assertEquals("Николай", getUser.getName()),
                () -> assertEquals("Nikolay@yandex.ru", getUser.getEmail()),
                () -> assertEquals("Nikolay1995", getUser.getLogin()),
                () -> assertEquals(LocalDate.of(1995, 5, 15), user.getBirthday()),
                () -> assertEquals(0, getUser.getFriends().size())
        );

        log.info("Тест запроса списка со всеми пользователями");
        List<User> users = userController.findAllUsers();
        assertEquals(2, users.size());

        log.info("Тест метода обновления пользователя");
        User userUpdate = User.builder().build();
        userUpdate.setId(2);
        userUpdate.setName("Ник");
        userUpdate.setEmail("Nik@yandex.ru");
        userUpdate.setLogin("Nik1996");
        userUpdate.setBirthday(LocalDate.of(1996, 5, 15));
        User upUser = userController.updateUser(userUpdate);

        assertAll("Проверка пользователя",
                () -> assertEquals(2, upUser.getId()),
                () -> assertEquals("Ник", upUser.getName()),
                () -> assertEquals("Nik@yandex.ru", upUser.getEmail()),
                () -> assertEquals("Nik1996", upUser.getLogin()),
                () -> assertEquals(LocalDate.of(1996, 5, 15), upUser.getBirthday()),
                () -> assertEquals(0, upUser.getFriends().size())
        );

        log.info("Тест метода добавления в друзья");
        userController.addFriend(1, 2);
        User userFriend = userController.getUser(1);
        User frinedUser = userController.getUser(2);
        assertEquals(1, userFriend.getFriends().size());
        assertEquals(0, frinedUser.getFriends().size());

        log.info("Тест метода удаления из друзей");
        userController.deleteFriend(1, 2);
        userController.deleteFriend(1, 2);
        userFriend = userController.getUser(1);
        frinedUser = userController.getUser(2);
        assertEquals(0, userFriend.getFriends().size());
        assertEquals(0, frinedUser.getFriends().size());

        log.info("Тест метода создания первого фильма");
        Film film = Film.builder().build();
        film.setName("Человек-паук");
        film.setDescription("Питера Паркера укусил радиоактивный паук!");
        film.setReleaseDate(LocalDate.of(2002, 4, 30));
        film.setDuration(121);
        film.setMpa(new Mpa(3));
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1));
        film.setGenres(genres);
        Film film1 = filmController.createFilm(film);

        assertAll("Проверка фильма",
                () -> assertEquals(1, film1.getId()),
                () -> assertEquals("Человек-паук", film1.getName()),
                () -> assertEquals("Питера Паркера укусил радиоактивный паук!", film1.getDescription()),
                () -> assertEquals(LocalDate.of(2002, 4, 30), film1.getReleaseDate()),
                () -> assertEquals(121, film1.getDuration()),
                () -> assertEquals(3, film1.getMpa().getId()),
                () -> assertEquals("PG-13", film1.getMpa().getName()),
                () -> assertEquals(1, film1.getGenres().size())
        );

        log.info("Тест метода создания второго фильма");
        Film film2 = Film.builder().build();
        film2.setName("Новый фильм");
        film2.setDescription("Описание нового фильма");
        film2.setReleaseDate(LocalDate.of(2000, 5, 15));
        film2.setDuration(121);
        film2.setMpa(new Mpa(2));
        Set<Genre> genres2 = new HashSet<>();
        genres.add(new Genre(2));
        film.setGenres(genres2);
        Film saveFilm2 = filmController.createFilm(film2);

        assertEquals(2, saveFilm2.getId());

        log.info("Тест метода запроса фильма по ИД");
        Film updateFilm = filmController.getFilm(2);
        assertEquals(2, updateFilm.getId());

        log.info("Тест метода обновления фильма");
        updateFilm.setName("Обнова");
        updateFilm.setDescription("Обновление описания");
        Film upFilm = filmController.updateFilm(updateFilm);
        assertAll("Проверка обновления",
                () -> assertEquals("Обнова", upFilm.getName()),
                () -> assertEquals("Обновление описания", upFilm.getDescription()),
                () -> assertEquals(2, upFilm.getId()));

        log.info("Тест метода списка всех фильмов");
        List<Film> films = filmController.findAllFilms();
        assertEquals(2, films.size());

        log.info("Тест метода запроса всех жанров");
        List<Genre> allGenres = genreController.findAllGenres();
        assertEquals(6, allGenres.size());

        log.info("Тест метода запроса жанра по ИД");
        Genre genre = genreController.findGenreById(2);
        assertEquals(2, genre.getId());
        assertEquals("Драма", genre.getName());

        log.info("Тест метода добавления лайка");
        filmController.addLikeFilm(1, 1);
        assertEquals(1, filmController.getFilm(1).getLikes().size());

        log.info("Тест методы запроса списка популярных фильмов");
        List<Film> popularFilms = filmController.popularFilms(10, 0, 0);
        assertEquals(2, popularFilms.size());

        log.info("Тест запроса рейтинга по ИД");
        Mpa mpa = ratingController.findRatingById(1);
        assertEquals("G", mpa.getName());

        log.info("Тест запроса листа всех рейтингов фильма");
        List<Mpa> mpas = ratingController.findAllRating();
        assertEquals(5, mpas.size());

        log.info("Тест удаления лайка");
        filmController.deleteLikeFilm(1, 1);
        assertEquals(0, filmController.getFilm(1).getLikes().size());
    }

}

