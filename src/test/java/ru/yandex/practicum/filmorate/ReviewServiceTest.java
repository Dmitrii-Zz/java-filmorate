package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.ReviewDBStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewServiceTest {

    private final ReviewDBStorage reviewDBStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @BeforeEach
    public void prepareTests() {
        Film film = new Film("Film1", "Description_of_film_1", LocalDate.now(), 1);
        Mpa mpa = new Mpa(1);
        film.setMpa(mpa);
        User user = new User("u1@ya.ru", "u1Login", "u1name", LocalDate.now());
        Review review = new Review("content_of_review1", true, 1,1);

        filmDbStorage.save(film);
        userDbStorage.save(user);
        reviewDBStorage.save(review);
    }



    @Test
    public void testFindReviewById() {


        Optional<Review> reviewOptional = Optional.ofNullable(reviewDBStorage.getReviewById(1));

        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("reviewId", 1)
                );
    }
}
