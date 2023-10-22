package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeFilmsStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled
@SpringBootTest
@AutoConfigureTestDatabase
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schemaTest.sql", "/dataTest.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RemoveEndpointTest {
    private final FilmStorage filmStorage;
    private final LikeFilmsStorage likeFilmsStorage;
    private final GenreStorage genreStorage;
    private final UserStorage userStorage;
    private final ReviewStorage reviewStorage;

    @Test
    public void testRemoveFilmById() {
        int id = 2;
        filmStorage.deleteFilmById(2);

        Optional<List<Film>> filmOptional = Optional.ofNullable(filmStorage.findAll());
        List<Integer> innerEnityIds =
                filmOptional.stream()
                        .flatMap(sys -> sys.stream())
                        .map(Film::getId)
                        .collect(Collectors.toList());

        boolean likes = likeFilmsStorage.getAllLikeFilmById(id).isEmpty();
        boolean genres = genreStorage.findGenreByFilmId(id).isEmpty();
        List<Review> rewiev = reviewStorage.getAllReviewsByFilmId(2, 0);
        assertThat((!innerEnityIds.contains(id)) && likes && genres && rewiev.isEmpty()).isTrue();
    }

    @Test
    public void testRemoveUserById() {
        int id = 2;
        userStorage.deleteUserById(2);

        Optional<List<User>> filmOptional = Optional.ofNullable(userStorage.findAll());
        List<Integer> innerEnityIds =
                filmOptional.stream()
                        .flatMap(sys -> sys.stream())
                        .map(User::getId)
                        .collect(Collectors.toList());

        boolean likes = !likeFilmsStorage.getAllLikeFilmById(id).contains(id);
        boolean recomend = userStorage.getFilmsRecomendation(id).isEmpty();

        Optional<List<Review>> reviewOptional = Optional.ofNullable(reviewStorage.getAllReviewsByFilmId(0, 0));
        List<Integer> innerUsersIds =
                reviewOptional.stream()
                        .flatMap(sys -> sys.stream())
                        .map(Review::getUserId)
                        .collect(Collectors.toList());

        boolean review = !innerUsersIds.contains(id);
        assertThat((!innerEnityIds.contains(id)) && likes && recomend && review).isTrue();
    }
}
