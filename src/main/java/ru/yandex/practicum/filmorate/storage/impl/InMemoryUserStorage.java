package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int userId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean findUserId(int id) {
        return users.containsKey(id);
    }

    @Override
    public User save(User user) {
        user.setId(userId);
        user.setFriends(new HashSet<>());
        users.put(userId++, user);
        return user;
    }

    @Override
    public User update(User user) {
        user.setFriends(users.get(user.getId()).getFriends());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }
}
