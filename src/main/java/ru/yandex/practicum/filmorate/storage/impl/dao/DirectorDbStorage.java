package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

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
        if (containsDirector(id)){
            return jdbcTemplate.queryForObject("SELECT * FROM directors WHERE director_id = ?",
                    (resultSet, rowNum) -> directorParameters(resultSet), id);}
        throw new DirectorNotFoundException("Director not found");
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

        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Возвращаем режиссера:" + director.toString());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        if (containsDirector(director.getId())){
        String sqlRequest = String.format("UPDATE directors " +
                        "SET name = '%s'" +
                        "WHERE director_id = '%d'",
                director.getName(), director.getId());
        jdbcTemplate.execute(sqlRequest);
        return director;}
        throw new DirectorNotFoundException("Director not found");
    }

    @Override
    public void deleteDirector(int id) {
       if (!containsDirector(id)){ throw new DirectorNotFoundException("Director not found");}
//        String sqlRequest = String.format("DELETE FROM directors WHERE director_id = %d", id);
//        jdbcTemplate.execute(sqlRequest);
        jdbcTemplate.update("delete from directors where id = ?", Long.valueOf(id));

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

    @Override
    public boolean containsDirector(int id) {
        Long count = jdbcTemplate.queryForObject("select count(director_id) from directors where director_id = ?", Long.class, id);
        return count == 1;
    }
}
