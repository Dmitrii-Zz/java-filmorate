package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeFilmsStorage;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Primary
@Slf4j
public class LikeFilmsDbRepository implements LikeFilmsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int filmId, int userId) {
        String sqlRequest = "MERGE INTO likes (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlRequest, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlRequest = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlRequest, filmId, userId);
    }

    @Override
    public Set<Integer> getAllLikeFilmById(int filmId) {
        log.info("Пришел запрос на лайки фильма id = " + filmId);
        String sqlRequest = "SELECT user_id FROM likes WHERE film_id = ?;";
        return new HashSet<>((jdbcTemplate.query(sqlRequest,
                (resultSet, rowNum) -> resultSet.getInt("user_id"), filmId)));
    }
}
