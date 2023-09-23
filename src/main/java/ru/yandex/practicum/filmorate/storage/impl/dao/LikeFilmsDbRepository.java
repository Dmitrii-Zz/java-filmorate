package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeFilmsStorage;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Primary
public class LikeFilmsDbRepository implements LikeFilmsStorage {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public void addLike(int filmId, int userId) {
        String sqlRequest = String.format("INSERT INTO likes (film_id, user_id) VALUES ('%d', '%d')", filmId, userId);
        jdbcTemplate.execute(sqlRequest);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlRequest = String.format("DELETE FROM likes WHERE film_id = %d AND user_id = %d", filmId, userId);
        jdbcTemplate.execute(sqlRequest);
    }

    @Override
    public Set<Integer> getAllLikeFilmById(int filmId) {
        String sqlRequest = String.format("SELECT user_id FROM likes WHERE film_id = %d", filmId);
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlRequest);
        Set<Integer> likes = new HashSet<>();
        while(sqlRowSet.next()) {
            likes.add(sqlRowSet.getInt("user_id"));
        }

        return likes;
    }
}
