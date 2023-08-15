package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.function.Executable;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

public class UserControllerTest {
    private static final UserController userController = new UserController();
    private static final String MESS_USER_VALIDATE_EXCEPTION =
            "Проверьте правильность заполнения данных почты, логина или даты рождения.";

    @BeforeAll
    public static void addUser() throws ValidationException {
        User user = new User();
        user.setName("Николай");
        user.setEmail("Nikolay@yandex.ru");
        user.setLogin("Nikolay1995");
        user.setBirthday(LocalDate.of(1995, 5, 15));
        userController.create(user);
    }

    @Test
    public void addUserTest() throws ValidationException {
        List<User> users = userController.findAll();
        assertEquals(1, users.size());

        User user1 = users.get(0);

        assertAll("Сравнение полей пользователя.",
                () -> assertEquals(1, user1.getId()),
                () -> assertEquals("Николай", user1.getName()),
                () -> assertEquals("Nikolay@yandex.ru", user1.getEmail()),
                () -> assertEquals("Nikolay1995", user1.getLogin()),
                () -> assertEquals(LocalDate.of(1995, 5, 15), user1.getBirthday()));
    }

    @Test
    public void addUserNullTest() {
        userValidateException(null);
    }
    @Test
    public void addUserWithoutEmailTest()  {
        User user = new User();
        user.setName("Николай");
        user.setLogin("Nikolay1995");
        user.setBirthday(LocalDate.of(1995, 5, 15));
        userValidateException(user);

        user.setEmail(" ");
        userValidateException(user);

        user.setEmail("");
        userValidateException(user);
    }

    @Test
    public void addUserWithoutNameTest() throws ValidationException {
        User user = new User();
        user.setEmail("Nikolay@yandex.ru");
        user.setLogin("Nikolay1995");
        user.setBirthday(LocalDate.of(1995, 5, 15));
        userController.create(user);
        assertEquals("Nikolay1995", user.getName());

        user.setName(" ");
        userController.create(user);
        assertEquals("Nikolay1995", user.getName());

        user.setName("");
        userController.create(user);
        assertEquals("Nikolay1995", user.getName());
    }

    @Test
    public void addUserWithoutLoginTest() {
        User user = new User();
        user.setName("Николай");
        user.setEmail("Nikolay@yandex.ru");
        user.setBirthday(LocalDate.of(1995, 5, 15));
        userValidateException(user);

        user.setLogin(" ");
        userValidateException(user);

        user.setLogin("");
        userValidateException(user);
    }

    @Test
    public void addUserLoginWithSpaceTest() {
        User user = new User();
        user.setName("Николай");
        user.setEmail("Nikolay@yandex.ru");
        user.setLogin("Nikolay 1995");
        user.setBirthday(LocalDate.of(1995, 5, 15));

        userValidateException(user);
    }

    @Test
    public void addUserWithoutBirthdayTest() {
        User user = new User();
        user.setName("Николай");
        user.setEmail("Nikolay@yandex.ru");
        user.setLogin("Nikolay1995");

        userValidateException(user);
    }

    @Test
    public void addUserFutureBirthdayTest() {
        User user = new User();
        user.setName("Николай");
        user.setEmail("Nikolay@yandex.ru");
        user.setLogin("Nikolay1995");
        user.setBirthday(LocalDate.of(2024, 8, 15));

        userValidateException(user);
    }



    @Test
    public void addUserWrongEmailFirstTest() {
        User user = new User();
        user.setName("Николай");
        user.setEmail("Nikolayyandex.ru");
        user.setLogin("Nikolay 1995");
        user.setBirthday(LocalDate.of(1995, 5, 15));

        userValidateException(user);
    }

    @Test
    public void addUserWrongEmailSecondTest() {
        User user = new User();
        user.setName("Николай");
        user.setEmail("Nikolay@");
        user.setLogin("Nikolay 1995");
        user.setBirthday(LocalDate.of(1995, 5, 15));

        userValidateException(user);
    }

    @Test
    public void updateUserTest() throws ValidationException {
        User user = new User();
        user.setId(1);
        user.setName("Коля");
        user.setEmail("Kolay@yandex.ru");
        user.setLogin("1995Kolay1995");
        user.setBirthday(LocalDate.of(1996, 6, 16));
        userController.update(user);

        List<User> users = userController.findAll();
        assertEquals(1, users.size());

        User user1 = users.get(0);

        assertAll("Сравнение полей пользователя.",
                () -> assertEquals(1, user1.getId()),
                () -> assertEquals("Коля", user1.getName()),
                () -> assertEquals("Kolay@yandex.ru", user1.getEmail()),
                () -> assertEquals("1995Kolay1995", user1.getLogin()),
                () -> assertEquals(LocalDate.of(1996, 6, 16), user1.getBirthday()));
    }

    @Test
    public void updateUserWrongId() {
        User user = new User();
        user.setId(2);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        userController.update(user);
                    }
                });

        assertEquals("Неверно указан id пользователя!", exception.getMessage());
    }

    @Test
    public void updateUserLoginWithSpaceTest() {
        User user = userController.findAll().get(0);
        user.setLogin("Nikolay 1995");
        updateUserValidateException(user);
    }

    @Test
    public void updateUserWithoutEmailTest() {
        User user = userController.findAll().get(0);
        user.setEmail("");
        updateUserValidateException(user);

        user.setEmail(" ");
        updateUserValidateException(user);

        user.setEmail("Nikolay@yandex.ru");
    }

    @Test
    public void updateUserWithoutNameTest() throws ValidationException {
        User user = userController.findAll().get(0);
        user.setLogin("Nikolay3000Ultra");
        user.setName(" ");
        userController.update(user);
        assertEquals("Nikolay3000Ultra", user.getName());
        user.setName("");
        userController.update(user);
        assertEquals("Nikolay3000Ultra", user.getName());
    }

    @Test
    public void updateUserWithoutLoginTest() {
        User user = userController.findAll().get(0);
        user.setLogin(" ");
        updateUserValidateException(user);
        user.setLogin("");
        updateUserValidateException(user);
    }

    @Test
    public void updateUserFutureBirthdayTest() {
        User user = userController.findAll().get(0);
        user.setBirthday(LocalDate.of(2023, 12, 30));
        userValidateException(user);
    }

    private void userValidateException(User user) {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        userController.create(user);
                    }
                });
        if (user == null) {
            assertEquals("Пользователь отсутствует!", exception.getMessage());
        } else {
            assertEquals(MESS_USER_VALIDATE_EXCEPTION, exception.getMessage());
        }

    }

    private void updateUserValidateException(User user) {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        userController.update(user);
                    }
                });

        assertEquals(MESS_USER_VALIDATE_EXCEPTION, exception.getMessage());
    }
}