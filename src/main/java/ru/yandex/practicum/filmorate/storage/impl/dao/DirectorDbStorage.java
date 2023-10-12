package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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

        String sqlRequest =
                String.format("INSERT INTO directors (director_id, name) VALUES (?, ?)");

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlRequest, new String[]{"director_id"});
            ps.setInt(1, director.getId());
            ps.setString(2, director.getName());
            return ps;
        }, keyHolder);

        director.setId((int) keyHolder.getKey());
        log.info("Возвращаем режиссера:" + director.toString());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sqlRequest = String.format("UPDATE directors " +
                                          "SET name = '%s'" +
                                          "WHERE director_id = '%d'",
                                       director.getName(), director.getId());
        jdbcTemplate.execute(sqlRequest);
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        String sqlRequest = String.format("DELETE FROM directors WHERE director_id = %d", id);
        jdbcTemplate.execute(sqlRequest);
    }

    @Override
    public boolean findDirectorById(int id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM directors WHERE director_id = ?", id);
        return directorRows.next();
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