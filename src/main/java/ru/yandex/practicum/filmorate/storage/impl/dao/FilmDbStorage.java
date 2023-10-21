package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmBuildingException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
                "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                        "VALUES (?, ?, ?, ?, ?)";

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
    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        String sqlRequest = null;
        if (genreId == 0 && year == 0) {
            sqlRequest = String.format("SELECT f.film_id, COUNT(l.film_id) " +
                    "FROM films AS f " +
                    "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(l.film_id) DESC " +
                    "LIMIT %d", count);
        }
        if (genreId != 0 && year == 0) {
            sqlRequest = String.format("SELECT f.*\n" +
                    "FROM films f\n" +
                    "INNER JOIN genre_film gf ON f.film_id = gf.film_id\n" +
                    "LEFT JOIN likes l ON f.film_id = l.film_id\n" +
                    "WHERE gf.genre_id = %d\n" +
                    "GROUP BY f.film_id\n" +
                    "ORDER BY COUNT(l.user_id) DESC;", genreId);
        }
        if (genreId == 0 && year != 0) {
            sqlRequest = String.format("SELECT f.* \n" +
                    "FROM films f\n" +
                    "LEFT JOIN likes l ON f.film_id = l.film_id\n" +
                    "WHERE EXTRACT(YEAR FROM f.release_date) = %d\n" +
                    "GROUP BY f.film_id\n" +
                    "ORDER BY COUNT(l.user_id) DESC;", year);
        }
        if (genreId != 0 && year != 0) {
            sqlRequest = String.format("SELECT f.* \n" +
                    "FROM films f\n" +
                    "LEFT JOIN likes l ON f.film_id = l.film_id\n" +
                    "JOIN genre_film gf ON f.film_id = gf.film_id\n" +
                    "WHERE EXTRACT(YEAR FROM f.release_date) = %d AND gf.genre_id = %d\n" +
                    "GROUP BY f.film_id\n" +
                    "ORDER BY COUNT(l.user_id) DESC;", year, genreId);
        }

        SqlRowSet popularFilms = jdbcTemplate.queryForRowSet(sqlRequest);
        List<Film> films = new ArrayList<>();
        while (popularFilms.next()) {
            films.add(getFilmById(popularFilms.getInt("film_id")));
        }

        return films;
    }

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

    @Override
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
        Set<Director> directors = Set.copyOf(directorRepository.findDirectorFilm(filmRows.getInt("film_id")));
        Integer rate = likeRepository.getAllLikeFilmById(filmRows.getInt("film_id")).size();
        return Film.builder()
                .name(filmRows.getString("name"))
                .description(filmRows.getString("description"))
                .releaseDate((filmRows.getDate("release_date")).toLocalDate())
                .duration(filmRows.getInt("duration"))
                .mpa(mpa)
                .id(filmRows.getInt("film_id"))
                .genres(genres)
                .directors(directors)
                .rate(rate)
                .likes(likes)
                .build();
    }

    private Film filmParameters(ResultSet resultSet) {
        try {
            String nameMpa = mpaRepository.findRatingById((resultSet.getInt("rating_id"))).getName();
            Mpa mpa = new Mpa(resultSet.getInt("rating_id"), nameMpa);
            Set<Genre> genres = genreRepository.findGenreByFilmId((resultSet.getInt("film_id")));
            Set<Integer> likes = likeRepository.getAllLikeFilmById(resultSet.getInt("film_id"));
            Set<Director> directors =
                    Set.copyOf(directorRepository.findDirectorFilm(resultSet.getInt("film_id")));
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
                    .rate(likeRepository.getAllLikeFilmById(resultSet.getInt("film_id")).size())
                    .build();
        } catch (Exception e) {
            throw new FilmBuildingException(e.getMessage());
        }
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by) {

        String sqlRequest = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sqlRequest,
                (resultSet, rowNum) -> filmParameters(resultSet));

        List<Film> findFilms = new ArrayList<>();

        if (((query != null) && (by != null)) && (by.contains("title") && !by.contains("director"))) {
            for (Film f : films) {
                if (f.getName().toLowerCase().contains(query.toLowerCase())) {
                    findFilms.add(f);
                }
            }
            return sortFilms(findFilms);
        }

        if (((query != null) && (by != null)) && (by.contains("director") && !by.contains("title"))) {
            for (Film f : films) {
                for (Director d : directorRepository.findDirectorFilm(f.getId())) {
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

        if (((query != null) && (by != null)) && (by.contains("title") && (by.contains("director")))) {
            for (Film f : films) {
                if (f.getName().toLowerCase().contains(query.toLowerCase())) {
                    findFilms.add(f);
                    continue;
                }
                for (Director d : directorRepository.findDirectorFilm(f.getId())) {
                    if (d.getName().toLowerCase().contains(query.toLowerCase())) {
                        if (findFilms.contains(f)) {
                            continue;
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

    private List<Film> sortFilms(List<Film> films) {
        return films.stream()
                .sorted((x1, x2) -> x2.getRate() - x1.getRate())
                .collect(Collectors.toList());
    }
}
