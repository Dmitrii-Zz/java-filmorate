package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private boolean isValidate;
    private int userId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    @PostMapping
    public User create(@Valid @RequestBody User user) throws ValidationException {

        if (user == null) {
            log.debug("Пользователь отсутствует!");
            throw new ValidationException("Пользователь отсутствует!");
        }

        isValidate = validateBirthday(user)
            && validateLogin(user.getLogin())
            && validateEmail(user);

        if (isValidate) {
            validateName(user);
            user.setId(userId);
            users.put(userId, user);
            userId++;
        } else {
            log.debug("Валидация при добавления пользователя не пройдена!");
            throw new ValidationException("Проверьте правильность заполнения данных почты, логина или даты рождения.");
        }

        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws ValidationException {

        if (!users.containsKey(user.getId())) {
            log.debug("Пользователь с id '{}' отсутствует", user.getId());
            throw new ValidationException("Неверно указан id пользователя!");
        }

        isValidate = validateBirthday(user)
                && validateLogin(user.getLogin())
                && validateEmail(user);

        if (isValidate) {
            validateName(user);
            users.put(user.getId(), user);
        } else {
            log.debug("Валидация при обновлении пользователя не пройдена!");
            throw new ValidationException("Проверьте правильность заполнения данных почты, логина или даты рождения.");
        }

        return user;
    }

    private boolean validateLogin(String login) {

        if (login != null) {
            if (login.isBlank()) return false;
            char[] massiveCharLogin = login.toCharArray();

            for (char c : massiveCharLogin) {
                if (c == ' ') return false;
            }

        } else return false;

        return true;
    }

    private void validateName(User user) {
        String name = user.getName();
        if (name == null) {
            user.setName(user.getLogin());
            return;
        }
        if (name.isBlank()) user.setName(user.getLogin());
    }

    private boolean validateBirthday(User user) {
        if (user.getBirthday() == null) return false;
        return LocalDate.now().isAfter(user.getBirthday());
    }

    private boolean validateEmail(User user) {
        if (user.getEmail() == null) return false;
        return !user.getEmail().isBlank();
    }
}
