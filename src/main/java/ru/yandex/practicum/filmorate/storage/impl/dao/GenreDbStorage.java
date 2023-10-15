package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Primary
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAllGenres() {
        List<Genre> genres = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genres");
        while (genreRows.next()) {
            genres.add(getGenreFromDb(genreRows));
        }

        return genres;
    }

    @Override
    public Genre findGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE genre_id = ?", id);
        genreRows.next();
        return getGenreFromDb(genreRows);
    }

    @Override
    public Set<Genre> findGenreByFilmId(int filmId) {
        Set<Genre> genres = new HashSet<>();
        String sqlRequest = String.format("SELECT gf.genre_id, g.name " +
                "FROM genre_film AS gf " +
                "LEFT OUTER JOIN genres AS g ON gf.genre_id = g.GENRE_ID " +
                "WHERE gf.film_id = %d " +
                "ORDER BY gf.genre_id ASC;", filmId);

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(sqlRequest);
        while (genresRows.next()) {
            genres.add(new Genre(genresRows.getInt("genre_id"), genresRows.getString("name")));
        }

        return genres;
    }

    @Override
    public void saveGenreFilm(int filmId, int genreId) {
        String sqlRequest =
                String.format("INSERT INTO genre_film (film_id, genre_id) VALUES ('%d', '%d')", filmId, genreId);
        jdbcTemplate.execute(sqlRequest);
    }

    @Override
    public void deleteGenreFilm(int filmID) {
        String sqlRequest =
                String.format("DELETE FROM genre_film WHERE film_id = %d;", filmID);

        jdbcTemplate.execute(sqlRequest);
    }

    private Genre getGenreFromDb(SqlRowSet genreRows) {
        return new Genre(genreRows.getInt("genre_id"), genreRows.getString("name"));
    }
}
