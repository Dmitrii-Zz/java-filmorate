package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserRepository userRepository = new UserRepository();

    @GetMapping
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        if (user == null) {
            log.debug("Пользователь отсутствует!");
            throw new ValidationException("Пользователь отсутствует!");
        }

        if (!validate(user)) {
            log.debug("Валидация при добавления пользователя не пройдена!");
            throw new ValidationException("Проверьте правильность заполнения данных почты, логина или даты рождения.");
        }

        validateName(user);
        return userRepository.save(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        if (!userRepository.findUserId(user.getId())) {
            log.debug("Пользователь с id '{}' отсутствует", user.getId());
            throw new ValidationException("Неверно указан id пользователя!");
        }

        if (!validate(user)) {
            log.debug("Валидация при обновлении пользователя не пройдена!");
            throw new ValidationException("Проверьте правильность заполнения данных почты, логина или даты рождения.");
        }

        validateName(user);
        return userRepository.update(user);
    }

    private boolean validate(User user) {
        return validateLogin(user.getLogin())
            && validateBirthday(user)
            && validateEmail(user);
    }

    private boolean validateLogin(String login) {

        if (login == null) {
            return false;
        }

        if (login.isBlank() || login.contains(" ")) {
            return false;
        }

        return true;
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

    private boolean validateBirthday(User user) {

        if (user.getBirthday() == null) {
            return false;
        }

        return LocalDate.now().isAfter(user.getBirthday());
    }

    private boolean validateEmail(User user) {

        if (user.getEmail() == null) {
            return false;
        }

        return !user.getEmail().isBlank();
    }
}
