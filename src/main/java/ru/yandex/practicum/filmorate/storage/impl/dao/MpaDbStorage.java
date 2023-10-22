package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

import java.sql.ResultSet;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class MpaDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAllRating() {
        return jdbcTemplate.query("SELECT * FROM rating",
                (resultSet, rowNum) -> mpaParameters(resultSet));
    }

    @Override
    public Mpa findRatingById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM rating WHERE rating_id = ?",
                (resultSet, rowNum) -> mpaParameters(resultSet), id);
    }

    private Mpa mpaParameters(ResultSet resultSet) {
        try {
            return Mpa.builder()
                    .id(resultSet.getInt("rating_id"))
                    .name(resultSet.getString("name"))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
