package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmRepository;

    @Override
    public User getUserById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id = ?",
                (resultSet, rowNum) -> userParameters(resultSet), id);
    }

    @Override
    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlRequest = "INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)";
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
        return jdbcTemplate.query("SELECT * FROM users",
                (resultSet, rowNum) -> userParameters(resultSet));
    }

    @Override
    public boolean findUserId(int id) {
        String sqlRequest = "SELECT EXISTS(SELECT * FROM users WHERE user_id = ?);";
        return jdbcTemplate.queryForObject(sqlRequest, Boolean.class, id);
    }

    @Override
    public void deleteUserById(int userId) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id=?", userId);
    }

    @Override
    public User update(User user) {
        String sqlRequest = "UPDATE users " +
                            "SET name = ?, email = ?, login = ?, birthday = ? " +
                            "WHERE user_id = ?";

        jdbcTemplate.update(sqlRequest, user.getName(),
                                        user.getEmail(),
                                        user.getLogin(),
                                        user.getBirthday(),
                                        user.getId());
        return getUserById(user.getId());
    }

    private User userParameters(ResultSet resultSet) {
        try {
            return User.builder()
                    .email(resultSet.getString("email"))
                    .login(resultSet.getString("login"))
                    .name(resultSet.getString("name"))
                    .id(resultSet.getInt("user_id"))
                    .birthday(resultSet.getDate("birthday").toLocalDate())
                    .friends(getAllFriends(resultSet.getInt("user_id")))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Set<Integer> getAllFriends(int userId) {
        return new HashSet<>(jdbcTemplate.query("SELECT friend_id FROM friends WHERE user_id = ?",
                (resultSet, rowNum) -> resultSet.getInt("friend_id"), userId));
    }

    @Override
    public List<Film> getFilmsRecomendation(int id) {
        try {
            String sqlRequest = "SELECT u.user_id " +
                    "FROM users u INNER JOIN likes l1 ON u.user_id = l1.user_id " +
                    "INNER JOIN likes l2 ON l1.film_id = l2.film_id AND l2.user_id = ? " +
                    "WHERE u.user_id != ? " +
                    "GROUP BY u.user_id " +
                    "ORDER BY COUNT(*) DESC " +
                    "LIMIT 1;";

            Integer userMostInters = jdbcTemplate.queryForObject(sqlRequest, Integer.class, id, id);

            sqlRequest = "SELECT f.* " +
                         "FROM films f " +
                         "LEFT JOIN likes l1 ON f.film_id = l1.film_id AND l1.user_id = ? " +
                         "LEFT JOIN likes l2 ON f.film_id = l2.film_id AND l2.user_id = ? " +
                         "WHERE l1.user_id IS NOT NULL AND l2.user_id IS NULL;";

            return jdbcTemplate.query(sqlRequest,
                    (resultSet, rowNum) -> filmRepository.getFilmById(resultSet.getInt("film_id")),
                    userMostInters, id);

        } catch (EmptyResultDataAccessException ex) {
            return new ArrayList<>();
        }
    }
}
