package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendshipStorage;
@Component
@RequiredArgsConstructor
@Primary
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId) {
        String sqlRequest = String.format("INSERT INTO friends (user_id, friend_id, friendship_status) VALUES " +
                "('%d', '%d', '%b')", userId, friendId, true);

        jdbcTemplate.execute(sqlRequest);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sqlRequest = String.format("DELETE FROM friends WHERE user_id = '%d' AND friend_id = '%d'", userId, friendId);
        jdbcTemplate.execute(sqlRequest);
    }
}
