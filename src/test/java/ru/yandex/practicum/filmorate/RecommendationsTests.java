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
public class RecommendationsTests {
    private final UserController userController;
    private final FilmController filmController;

    @Test
    void getFilmsRecomendation() {
        Film film1 = Film.builder().name("Film1").description("deskr").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).genres(Set.of(new Genre(1, "Драма"), new Genre(2, "Комедия"))).id(1).mpa(new Mpa(1, "name")).build();
        Film film2 = Film.builder().name("Film2").description("deskr").releaseDate(LocalDate.of(1999, 1, 1)).duration(120).genres(Set.of(new Genre(1, "Драма"))).id(2).mpa(new Mpa(1, "name")).build();
        Film film3 = Film.builder().name("Film3").description("deskr").releaseDate(LocalDate.of(1980, 1, 1)).duration(120).genres(Set.of(new Genre(2, "Комедия"))).id(3).mpa(new Mpa(1, "name")).build();
        User user1 = User.builder().name("name1").login("login1").email("email1@mail.ru").birthday(LocalDate.of(2000, 1, 1)).id(1).build();
        User user2 = User.builder().name("name2").login("login2").email("email2@mail.ru").birthday(LocalDate.of(2000, 1, 1)).id(2).build();
        User user3 = User.builder().name("name3").login("login3").email("email3@mail.ru").birthday(LocalDate.of(2000, 1, 1)).id(3).build();
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        filmController.createFilm(film3);
        userController.createUser(user1);
        userController.createUser(user2);
        userController.createUser(user3);

        List<Film> recomendations = userController.getFilmsRecomendation(1);
        log.info("Тест запроса списка рекомендаций без лайканых фильмов");
        Assertions.assertEquals(0, recomendations.size(), "Неверный вывод при отсутствии лайков");

        filmController.addLikeFilm(3, 1);
        filmController.addLikeFilm(1, 2);
        filmController.addLikeFilm(2, 2);
        filmController.addLikeFilm(3, 2);
        filmController.addLikeFilm(1, 1);
        filmController.addLikeFilm(2, 3);

        log.info("Тест запроса списка рекомендаций");
        recomendations = userController.getFilmsRecomendation(1);
        Assertions.assertEquals(2, recomendations.get(0).getId(), "Неверный вывод рекомендаций");
    }
}
