package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import static ru.yandex.practicum.filmorate.Constants.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final InMemoryUserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        validationId(id);
        return userStorage.getUserById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        if (user == null) {
            throw new UserNotFoundException("В запросе отсутствует пользователь.");
        }

        userValidate(user);
        validateName(user);
        return userStorage.save(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        if (user == null) {
            throw new UserNotFoundException("В запросе отсутствует пользователь.");
        }

        validationId(user.getId());
        userValidate(user);
        validateName(user);
        return userStorage.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        validationIdUserAndOtherId(id, friendId);
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        userService.addFriend(user, friend);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        validationIdUserAndOtherId(id, friendId);
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        userService.removeFriend(user, friend);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable int id) {
        validationId(id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        validationIdUserAndOtherId(id, otherId);
        return userService.findMutualFriends(id, otherId);
    }

    private void validationIdUserAndOtherId(int id, int id1) {
        validationId(id);
        validationId(id1);

        if (id == id1) {
            throw new UserValidationException("Нельзя добавить себя в список друзей.");
        }

        if (!userStorage.findUserId(id) || !userStorage.findUserId(id1)) {
            throw new UserNotFoundException("Пользователь с данным id ");
        }
    }

    public void validationId(int id) {
        if (id < CORRECT_ID) {
            throw new UserNotFoundException(String.format("Передан неверный ИД пользователя - id = \"%d\" ", id));
        }

        if (!(userStorage.findUserId(id))) {
            throw new UserNotFoundException(String.format("Пользователь с \"%d\" не найден", id));
        }
    }

    private void userValidate(User user) {
        if (user.getLogin() == null) {
            throw new UserValidationException("Не передан логин - Login = null");
        }

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new UserValidationException("Логин не должен быть пустым и содержать пробелов.");
        }

        if (user.getBirthday() == null) {
            throw new UserValidationException("Не передана дата рождения - Birthday = null");
        }

        if (!LocalDate.now().isAfter(user.getBirthday())) {
            throw new UserValidationException("Некорректная дата рождения.");
        }

        if (user.getEmail() == null) {
            throw new UserValidationException("Не передан email - Email = null");
        }

        if (user.getEmail().isBlank()) {
            throw new UserValidationException("Передан пустой логин.");
        }
    }

    private void validateName(User user) {
        String name = user.getName();

        if (name == null) {
            user.setName(user.getLogin());
            return;
        }

        if (name.isBlank()) {
            user.setName(user.getLogin());
        }
    }
}