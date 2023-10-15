package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    List<User> findAll();

    boolean findUserId(int id);

    User save(User user);

    User update(User user);

    User getUserById(int id);

    List<Film> getFilmsRecomendation(int id);

    void deleteUserById(int userId);
}
