package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User getUserById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);
        userRows.next();
        return getUserFromDb(userRows);
    }

    @Override
    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlRequest =
                String.format("INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)");
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlRequest, new String[]{"user_id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);

        return getUserById((int) keyHolder.getKey());
    }


    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users");
        while (userRows.next()) {
            users.add(getUserFromDb(userRows));
        }

        return users;
    }

    @Override
    public boolean findUserId(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);
        return userRows.next();
    }

    @Override
    public User update(User user) {
        String sqlRequest =
                String.format("UPDATE users " +
                                "SET name = '%s', email = '%s', login = '%s', birthday = '%s' " +
                                "WHERE user_id = '%d'",
                        user.getName(), user.getEmail(), user.getLogin(),
                        user.getBirthday(), user.getId());
        jdbcTemplate.execute(sqlRequest);
        return getUserById(user.getId());
    }

    @Override
    public void deleteUserById(int userId) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id=?", userId);
    }

    private User getUserFromDb(SqlRowSet userRows) {
        return User.builder()
                .email(userRows.getString("email"))
                .login(userRows.getString("login"))
                .name(userRows.getString("name"))
                .id(userRows.getInt("user_id"))
                .birthday((userRows.getDate("birthday")).toLocalDate())
                .friends(getAllFriends(userRows.getInt("user_id")))
                .build();
    }

    private Set<Integer> getAllFriends(int userId) {
        Set<Integer> friends = new HashSet<>();
        String sqlRequest = String.format("SELECT friend_id FROM friends WHERE user_id = %d", userId);
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlRequest);
        while (friendsRows.next()) {
            friends.add(friendsRows.getInt("friend_id"));
        }

        return friends;
    }
}
