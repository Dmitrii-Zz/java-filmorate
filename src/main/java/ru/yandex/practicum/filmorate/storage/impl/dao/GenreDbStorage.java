package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genres",
                (resultSet, rowNum) -> genreParameters(resultSet));
    }

    @Override
    public Genre findGenreById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM genres WHERE genre_id = ?",
                (resultSet, rowNum) -> genreParameters(resultSet), id);
    }

    @Override
    public Set<Genre> findGenreByFilmId(int filmId) {
        String sqlRequest = "SELECT gf.genre_id, g.name " +
                            "FROM genre_film AS gf " +
                            "LEFT OUTER JOIN genres AS g ON gf.genre_id = g.GENRE_ID " +
                            "WHERE gf.film_id = ? " +
                            "ORDER BY gf.genre_id ASC;";

        List<Genre> genres = (jdbcTemplate.query(sqlRequest,
                (resultSet, rowNum) -> genreParameters(resultSet), filmId)).stream()
                .sorted((x1, x2) -> x2.getId() - x1.getId()).collect(Collectors.toList());

        return Set.copyOf(genres);
    }

    @Override
    public void saveGenreFilm(int filmId, int genreId) {
        String sqlRequest = "INSERT INTO genre_film (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlRequest, filmId, genreId);
    }

    @Override
    public void deleteGenreFilm(int filmID) {
        String sqlRequest = "DELETE FROM genre_film WHERE film_id = ?;";
        jdbcTemplate.update(sqlRequest, filmID);
    }

    private Genre genreParameters(ResultSet resultSet) {
        try {
            return Genre.builder()
                        .id(resultSet.getInt("genre_id"))
                        .name(resultSet.getString("name"))
                        .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}