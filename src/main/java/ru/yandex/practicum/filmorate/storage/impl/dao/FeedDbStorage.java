package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.interfaces.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> getUserFeeds(int id) {
        String sqlGetFeeds = "SELECT * FROM feeds WHERE user_id=?";
        return jdbcTemplate.query(sqlGetFeeds, (rs, rowNum) -> makeFeed(rs), id);
    }

    private Feed makeFeed(ResultSet rs) throws SQLException {
        Feed feed = new Feed();
        feed.setEventId(rs.getInt("event_id"));
        feed.setTimestamp(rs.getLong("timestamp"));
        feed.setUserId(rs.getInt("user_id"));
        feed.setEventType(rs.getString("event_type"));
        feed.setOperation(rs.getString("operation"));
        feed.setEntityId(rs.getInt("entity_id"));
        return feed;
    }

    @Override
    public void addFeed(Feed feed) {
        String sqlAddFeed = "INSERT INTO feeds(timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlAddFeed,
                feed.getTimestamp(),
                feed.getUserId(),
                feed.getEventType(),
                feed.getOperation(),
                feed.getEntityId());
    }
}
