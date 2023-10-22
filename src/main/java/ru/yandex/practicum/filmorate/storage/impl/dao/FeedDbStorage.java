package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.interfaces.FeedStorage;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> getUserFeeds(int id) {
        String sqlGetFeeds = "SELECT * FROM feeds WHERE user_id = ?";
        return jdbcTemplate.query(sqlGetFeeds, (rs, rowNum) -> makeFeed(rs), id);
    }

    private Feed makeFeed(ResultSet rs) {
        try {
            log.info("id события: " + rs.getInt("event_id"));
            return Feed.builder()
                    .eventId(rs.getInt("event_id"))
                    .timestamp(rs.getLong("timestamp"))
                    .userId(rs.getInt("user_id"))
                    .eventType(EventType.valueOf(rs.getString("event_type")))
                    .operation(Operation.valueOf(rs.getString("operation")))
                    .entityId(rs.getInt("entity_id"))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFeed(int userId, int entityId, EventType type, Operation operation) {
        String sqlAddFeed = "INSERT INTO feeds(timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlAddFeed,
                Instant.now().toEpochMilli(),
                userId,
                type.toString(),
                operation.toString(),
                entityId);
    }
}
