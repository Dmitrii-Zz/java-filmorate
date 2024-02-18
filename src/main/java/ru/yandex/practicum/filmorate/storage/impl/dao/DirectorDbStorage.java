package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM directors",
                (resulSet, rowNum) -> directorParameters(resulSet));
    }

    @Override
    public Director getDirectorById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM directors WHERE director_id = ?",
                (resultSet, rowNum) -> directorParameters(resultSet), id);
    }

    @Override
    public Director createDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlRequest = "INSERT INTO directors (name) VALUES (?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlRequest, new String[]{"director_id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId((int) keyHolder.getKey());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sqlRequest = "UPDATE directors SET name = ? WHERE director_id = ?;";
        jdbcTemplate.update(sqlRequest, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        String sqlRequest = "DELETE FROM directors WHERE director_id = ?;";
        jdbcTemplate.update(sqlRequest, id);
    }

    @Override
    public boolean findDirectorById(int id) {
        String sqlRequest = "SELECT EXISTS(SELECT * FROM directors WHERE director_id = ?)";
        return jdbcTemplate.queryForObject(sqlRequest, Boolean.class, id);
    }

    @Override
    public List<Director> findDirectorFilm(int filmId) {
        String sqlRequest = "SELECT d.* FROM DIRECTORS AS d\n" +
                            "LEFT OUTER JOIN DIRECTOR_FILM df ON d.DIRECTOR_ID = df.DIRECTOR_ID \n" +
                            "WHERE df.FILM_ID = ?;";

        return jdbcTemplate.query(sqlRequest,
                (resulSet, rowNum) -> directorParameters(resulSet), filmId);
    }

    private Director directorParameters(ResultSet resultSet) {
        try {
            return Director.builder()
                    .id(resultSet.getInt("director_id"))
                    .name(resultSet.getString("name"))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
