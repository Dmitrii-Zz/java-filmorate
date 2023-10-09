package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class MpaDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAllRating() {
        List<Mpa> mpas = new ArrayList<>();
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("SELECT * FROM rating");
        while (ratingRows.next()) {
            mpas.add(getRatingFromDb(ratingRows));
        }

        return mpas;
    }

    @Override
    public Mpa findRatingById(int id) {
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("SELECT * FROM rating WHERE rating_id = ?", id);
        if (ratingRows.next()) {
            return getRatingFromDb(ratingRows);
        }

        throw new RatingNotFoundException(String.format("Рейтинг с id = '%d', не существует", id));
    }

    private Mpa getRatingFromDb(SqlRowSet ratingRows) {
        return new Mpa(ratingRows.getInt("rating_id"), ratingRows.getString("name"));
    }
}
