package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeFilmsStorage;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaRepository;
    private final GenreStorage genreRepository;
    private final LikeFilmsStorage likeRepository;
    @Override
    public List<Film> findAll() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet("SELECT * FROM films");

        while(filmsRows.next()) {
            films.add(getFilmFromDb(filmsRows));
        }

        return films;
    }

    @Override
    public boolean findFilmId(int id) {
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE film_id = ?", id);
        return filmsRows.next();
    }

    @Override
    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlRequest =
                String.format("INSERT INTO films (name, description, release_date, duration, rating_id) " +
                        "VALUES (?, ?, ?, ?, ?)");

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlRequest, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
            }, keyHolder);

        film.setId((int) keyHolder.getKey());
        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                genreRepository.saveGenreFilm(film.getId(), genre.getId());
            }
        }

        film.setGenres(genreRepository.findGenreByFilmId(film.getId()));
        film.setMpa(mpaRepository.findRatingById(film.getMpa().getId()));
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlRequest =
                String.format("UPDATE films " +
                              "SET name = '%s', description = '%s', release_date = '%s', " +
                              "duration = '%d', rating_id = '%d' " +
                              "WHERE film_id = '%d'",
                        film.getName(), film.getDescription(), film.getReleaseDate(),
                        film.getDuration(), film.getMpa().getId(), film.getId());

        jdbcTemplate.execute(sqlRequest);

        Set<Genre> genres = film.getGenres();

        if (genres != null) {
            genreRepository.deleteGenreFilm(film.getId());
            for (Genre genre : genres) {
                genreRepository.saveGenreFilm(film.getId(), genre.getId());
            }
        }

        film.setGenres(genreRepository.findGenreByFilmId(film.getId()));
        film.setMpa(mpaRepository.findRatingById(film.getMpa().getId()));

        return film;
    }

    @Override
    public Film getFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE film_id = ?", id);
        filmRows.next();
        return getFilmFromDb(filmRows);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlRequest = String.format("SELECT f.film_id, COUNT(l.film_id) " +
                                          "FROM films AS f " +
                                          "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id " +
                                          "GROUP BY f.film_id " +
                                          "ORDER BY COUNT(l.film_id) DESC " +
                                          "LIMIT %d", count);

        SqlRowSet popularFilms = jdbcTemplate.queryForRowSet(sqlRequest);
        List<Film> films = new ArrayList<>();

        while(popularFilms.next()) {
            films.add(getFilmById(popularFilms.getInt("film_id")));
        }

        return films;
    }

    private Film getFilmFromDb(SqlRowSet filmRows) {
        String nameMpa = mpaRepository.findRatingById((filmRows.getInt("rating_id"))).getName();
        Mpa mpa = new Mpa(filmRows.getInt("rating_id"), nameMpa);
        Set<Genre> genres = genreRepository.findGenreByFilmId((filmRows.getInt("film_id")));
        Set<Integer> likes = likeRepository.getAllLikeFilmById(filmRows.getInt("film_id"));

        return Film.builder()
                .name(filmRows.getString("name"))
                .description(filmRows.getString("description"))
                .releaseDate((filmRows.getDate("release_date")).toLocalDate())
                .duration(filmRows.getInt("duration"))
                .mpa(mpa)
                .id(filmRows.getInt("film_id"))
                .genres(genres)
                .likes(likes)
                .build();
    }
}
