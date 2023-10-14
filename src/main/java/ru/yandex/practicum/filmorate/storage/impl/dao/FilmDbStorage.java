package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorFilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeFilmsStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaRepository;
    private final GenreStorage genreRepository;
    private final LikeFilmsStorage likeRepository;
    private final DirectorFilmStorage directorFilmRepository;
    private final DirectorStorage directorRepository;

    public Set<Director> findDirectorsFilm(int id) {
        List<Integer> directorsIds = jdbcTemplate.queryForList("SELECT director_film.director_id FROM director_film WHERE film_id=?", Integer.class, id);
        Set<Director> directors = new HashSet<>();
        for (Integer j : directorsIds) {
            Director director = new Director();
            String str = jdbcTemplate.queryForObject("SELECT name FROM directors WHERE director_id=?", String.class, j);
            director.setName(str);
            director.setId(j);
            if (directors.contains(director)) {
                break;
            }
            directors.add(director);
        }
        return directors;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet("SELECT * FROM films");
        while (filmsRows.next()) {
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

        Set<Director> directors = film.getDirectors();
        if (directors != null) {
            for (Director director : directors) {
                directorFilmRepository.addFilmByDirector(film.getId(), director.getId());
                director.setName(directorRepository.getDirectorById(director.getId()).getName());
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
        } else {
            directorFilmRepository.deleteDirectorsByFilmId(film.getId());
        }

        Set<Director> directors = film.getDirectors();
        if (directors != null) {
            for (Director director : directors) {
                directorFilmRepository.addFilmByDirector(film.getId(), director.getId());
                director.setName(directorRepository.getDirectorById(director.getId()).getName());
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
        while (popularFilms.next()) {
            films.add(getFilmById(popularFilms.getInt("film_id")));
        }

        return films;
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by, int count) {
        List<Film> films = getPopularFilms(count);
        List<Film> findFilms = new ArrayList<>();

        if ((query != null) && ((by != null) && (by.contains("title") && !by.contains("director")))) {
            for (Film f : films) {
                if (f.getName().toLowerCase().contains(query.toLowerCase())) {
                    findFilms.add(f);
                }
            }
            return sortFilms(findFilms);
        }

        if ((query != null) && ((by != null) && (by.contains("director") && !by.contains("title")))) {
            for (Film f : films) {
                for (Director d : findDirectorsFilm(f.getId())) {
                    if (d.getName().toLowerCase().contains(query.toLowerCase())) {
                        if (findFilms.contains(f)) {
                            break;
                        }
                        findFilms.add(f);
                    }
                }
            }
            return sortFilms(findFilms);
        }

        if ((query != null) && ((by != null) && (by.contains("title") && (by.contains("director"))))) {
            for (Film f : films) {
                for (Director d : findDirectorsFilm(f.getId())) {
                    if (d.getName().toLowerCase().contains(query.toLowerCase()) || (f.getName().toLowerCase().contains(query.toLowerCase()))) {
                        if (findFilms.contains(f)) {
                            break;
                        }
                        findFilms.add(f);
                    }
                }
            }
            return sortFilms(findFilms);
        }

        if ((query == null) && (by == null)) {
            return sortFilms(films);
        }

        throw new FilmNotFoundException("Wrong parameters, 'query' can't be empty and 'by' mast be one of: title / director / title,director");
    }

    public List<Film> sortFilms(List<Film> films) {
        return films.stream()
                .sorted((x1, x2) -> x2.getRate() - x1.getRate())
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getFilmsByDirector(int id, String sortBy) {
        String sqlRequest;

        if (sortBy.equals("year")) {
            sqlRequest = String.format("SELECT f.* FROM DIRECTOR_FILM df\n" +
                                  "LEFT OUTER JOIN films AS f ON df.FILM_ID = f.FILM_ID\n" +
                                  "WHERE df.director_id = %d\n" +
                                  "ORDER BY f.RELEASE_DATE ASC", id);
        } else {
            sqlRequest = String.format("SELECT f.*, COUNT(l.FILM_ID) FROM DIRECTOR_FILM df\n" +
                            "LEFT OUTER JOIN films AS f ON df.FILM_ID = f.film_id\n" +
                            "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id\n" +
                            "WHERE df.director_id = %d\n" +
                            "GROUP BY f.film_id\n" +
                            "ORDER BY COUNT(l.user_id) DESC;", id);
        }

        return jdbcTemplate.query(sqlRequest,
                (resultSet, rowNum) -> filmParameters(resultSet));
    }

    public void deleteFilmById(int filmId) {
        jdbcTemplate.update("DELETE FROM films WHERE film_id=?", filmId);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String sqlRequest = "SELECT f.film_id, COUNT(l.film_id) " +
                "FROM films AS f " +
                "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id " +
                "WHERE f.film_id IN (SELECT l1.film_id FROM likes AS l1 " +
                "LEFT JOIN likes AS l2 ON l1.film_id=l2.film_id " +
                "WHERE l1.user_id=? AND l2.user_id=?) " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.film_id) DESC";

        SqlRowSet popularFilms = jdbcTemplate.queryForRowSet(sqlRequest, userId, friendId);
        List<Film> films = new ArrayList<>();
        while (popularFilms.next()) {
            films.add(getFilmById(popularFilms.getInt("film_id")));
        }

        return films;
    }

    private Film getFilmFromDb(SqlRowSet filmRows) {
        String nameMpa = mpaRepository.findRatingById((filmRows.getInt("rating_id"))).getName();
        Mpa mpa = new Mpa(filmRows.getInt("rating_id"), nameMpa);
        Set<Genre> genres = genreRepository.findGenreByFilmId((filmRows.getInt("film_id")));
        Set<Integer> likes = likeRepository.getAllLikeFilmById(filmRows.getInt("film_id"));
        Set<Director> directors = directorRepository.findDirectorFilm(filmRows.getInt("film_id"));

        return Film.builder()
                .name(filmRows.getString("name"))
                .description(filmRows.getString("description"))
                .releaseDate((filmRows.getDate("release_date")).toLocalDate())
                .duration(filmRows.getInt("duration"))
                .mpa(mpa)
                .id(filmRows.getInt("film_id"))
                .genres(genres)
                .rate(jdbcTemplate.queryForObject("SELECT count(user_id) FROM likes WHERE film_id=?", Integer.class, filmRows.getInt("film_id")))
                .directors(directors)
                .likes(likes)
                .build();
    }

    private Film filmParameters(ResultSet resultSet) {
        try {
            String nameMpa = mpaRepository.findRatingById((resultSet.getInt("rating_id"))).getName();
            Mpa mpa = new Mpa(resultSet.getInt("rating_id"), nameMpa);
            Set<Genre> genres = genreRepository.findGenreByFilmId((resultSet.getInt("film_id")));
            Set<Integer> likes = likeRepository.getAllLikeFilmById(resultSet.getInt("film_id"));
            Set<Director> directors = directorRepository.findDirectorFilm(resultSet.getInt("film_id"));
            return Film.builder()
                    .name(resultSet.getString("name"))
                    .description(resultSet.getString("description"))
                    .releaseDate((resultSet.getDate("release_date")).toLocalDate())
                    .duration(resultSet.getInt("duration"))
                    .mpa(mpa)
                    .id(resultSet.getInt("film_id"))
                    .genres(genres)
                    .likes(likes)
                    .directors(directors)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
