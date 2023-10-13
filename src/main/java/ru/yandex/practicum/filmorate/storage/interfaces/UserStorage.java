package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    boolean findUserId(int id);

    User save(User user);

    User update(User user);

    User getUserById(int id);

   void deleteUserById(int userId);
}
