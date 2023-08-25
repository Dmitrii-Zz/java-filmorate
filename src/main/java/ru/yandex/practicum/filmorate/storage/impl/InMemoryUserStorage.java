package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserStorage {
    private int userId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public boolean findUserId(int id) {
        return users.containsKey(id);
    }

    public User save(User user) {
        user.setId(userId);
        users.put(userId++, user);
        return user;
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }
}
