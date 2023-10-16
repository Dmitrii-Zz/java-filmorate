package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schemaTest.sql", "/dataTest.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SearchTest {
    private final FilmStorage filmStorage;

    @Test
    public void testSearchFilmWithoutParameters() {

        List<Integer> films = new ArrayList<>();
        films.add(2);
        films.add(1);
        films.add(3);

        Optional<List<Film>> filmOptional = Optional.ofNullable(filmStorage.searchFilms(null, null));
        List<Integer> innerEnityIds =
                filmOptional.stream()
                        .flatMap(sys -> sys.stream())
                        .map(Film::getId)
                        .collect(Collectors.toList());
        System.out.println(films);
        System.out.println(innerEnityIds);

        assertThat(innerEnityIds.equals(films)).isTrue();
    }

    @Test
    public void testSearchFilmWithDirector() {

        List<Integer> films = new ArrayList<>();
        films.add(2);
        films.add(3);

        Optional<List<Film>> filmOptional = Optional.ofNullable(filmStorage.searchFilms("Brush", Collections.singletonList("director")));
        List<Integer> filmsIds =
                filmOptional.stream()
                        .flatMap(sys -> sys.stream())
                        .map(Film::getId)
                        .collect(Collectors.toList());
        assertThat(filmsIds.equals(films)).isTrue();
    }

    @Test
    public void testSearchFilmWithTitle() {

        List<Integer> films = new ArrayList<>();
        films.add(2);
        films.add(1);

        Optional<List<Film>> filmOptional = Optional.ofNullable(filmStorage.searchFilms("white", Collections.singletonList("title")));
        List<Integer> filmsIds =
                filmOptional.stream()
                        .flatMap(sys -> sys.stream())
                        .map(Film::getId)
                        .collect(Collectors.toList());
        System.out.println(films);
        System.out.println(filmsIds);

        assertThat(filmsIds.equals(films)).isTrue();
    }

    @Test
    public void testSearchFilmWithDirectorAndTitle() {

        List<Integer> films = new ArrayList<>();
        films.add(2);
        films.add(1);

        Optional<List<Film>> filmOptional = Optional.ofNullable(filmStorage.searchFilms("JA", Arrays.asList("title", "director")));
        List<Integer> innerEnityIds =
                filmOptional.stream()
                        .flatMap(sys -> sys.stream())
                        .map(Film::getId)
                        .collect(Collectors.toList());
        System.out.println(films);
        System.out.println(innerEnityIds);

        assertThat(innerEnityIds.equals(films)).isTrue();
    }

}



