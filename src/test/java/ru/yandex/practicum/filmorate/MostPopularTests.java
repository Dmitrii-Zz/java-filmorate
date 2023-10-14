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
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MostPopularTests {
    private final UserController userController;
    private final FilmController filmController;

    @Test
    void mostPopulartest() {
        Film film1 = Film.builder().name("Film1").description("deskr").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).genres(Set.of(new Genre(1, "Драма"), new Genre(2, "Комедия"))).mpa(new Mpa(1, "name")).build();
        Film film2 = Film.builder().name("Film2").description("deskr").releaseDate(LocalDate.of(1999, 1, 1)).duration(120).genres(Set.of(new Genre(1, "Драма"))).mpa(new Mpa(1, "name")).build();
        Film film3 = Film.builder().name("Film3").description("deskr").releaseDate(LocalDate.of(1980, 1, 1)).duration(120).genres(Set.of(new Genre(2, "Комедия"))).mpa(new Mpa(1, "name")).build();
        User user1 = User.builder().name("name1").login("login1").email("email1@mail.ru").birthday(LocalDate.of(2000, 1, 1)).id(1).build();
        User user2 = User.builder().name("name2").login("login2").email("email2@mail.ru").birthday(LocalDate.of(2000, 1, 1)).id(2).build();
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        filmController.createFilm(film3);
        userController.createUser(user1);
        userController.createUser(user2);
        filmController.addLikeFilm(3, 1);
        filmController.addLikeFilm(2, 1);
        filmController.addLikeFilm(2, 2);

        log.info("Тест запроса популярных фильмов с фильтром по годам");
        List<Film> popularFilm = filmController.popularFilms(10, 0, 1999);
        Assertions.assertEquals(filmController.getFilm(2), popularFilm.get(0), "При фильтре по году выдает неверный фильм");

        log.info("Тест запроса популярных фильмов с фильтром по жанру");
        popularFilm = filmController.popularFilms(10, 2, 0);
        Assertions.assertEquals(filmController.getFilm(3), popularFilm.get(0), "При фильтре по жанру выдает неверный фильм");

        log.info("Тест запроса популярных фильмов с фильтром по годам и жанру");
        popularFilm = filmController.popularFilms(10, 1, 2000);
        Assertions.assertEquals(filmController.getFilm(1), popularFilm.get(0), "При фильтре по жанру и году выдает неверный фильм");
        //пусто


    }
}
