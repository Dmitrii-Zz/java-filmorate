package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private final UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

    @BeforeEach
    public void createUser() {
        User user = User.builder().build();
        user.setName("Николай");
        user.setEmail("Nikolay@yandex.ru");
        user.setLogin("Nikolay1995");
        user.setBirthday(LocalDate.of(1995, 5, 15));
        userController.createUser(user);
    }

    @Test
    public void createUserTest() {
        List<User> users = userController.findAllUsers();
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
        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.createUser(null);
                    }
                });

        assertEquals("В запросе отсутствует пользователь.", exception.getMessage());
    }

    @Test
    public void addUserWithoutEmailTest() {
        User user = userController.getUser(1);
        user.setEmail(null);

        final UserValidationException exception = assertThrows(
                UserValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.createUser(user);
                    }
                });

        assertEquals("Не передан email - Email = null", exception.getMessage());

        user.setEmail(" ");

        final UserValidationException exception1 = assertThrows(
                UserValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.createUser(user);
                    }
                });

        assertEquals("Передан пустой логин.", exception1.getMessage());

        user.setEmail("");

        final UserValidationException exception2 = assertThrows(
                UserValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.createUser(user);
                    }
                });

        assertEquals("Передан пустой логин.", exception2.getMessage());
    }

    @Test
    public void addUserWithoutNameTest() {
        User user = User.builder().build();
        user.setEmail("Nikolay@yandex.ru");
        user.setLogin("Nikolay1995");
        user.setBirthday(LocalDate.of(1995, 5, 15));
        userController.createUser(user);
        assertEquals("Nikolay1995", user.getName());

        user.setName(" ");
        userController.createUser(user);
        assertEquals("Nikolay1995", user.getName());

        user.setName("");
        userController.createUser(user);
        assertEquals("Nikolay1995", user.getName());
    }

    @Test
    public void addUserWithoutLoginTest() {
        User user = userController.getUser(1);
        user.setLogin(null);

        final UserValidationException exception = assertThrows(
                UserValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.createUser(user);
                    }
                });

        assertEquals("Не передан логин - Login = null", exception.getMessage());


        user.setLogin(" ");

        final UserValidationException exception2 = assertThrows(
                UserValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.createUser(user);
                    }
                });

        assertEquals("Логин не должен быть пустым и содержать пробелов.", exception2.getMessage());

        user.setLogin("");

        final UserValidationException exception3 = assertThrows(
                UserValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.createUser(user);
                    }
                });

        assertEquals("Логин не должен быть пустым и содержать пробелов.", exception3.getMessage());
    }

    @Test
    public void addUserLoginWithSpaceTest() {
        User user = userController.getUser(1);
        user.setLogin("Nikolay 1995");

        final UserValidationException exception = assertThrows(
                UserValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.createUser(user);
                    }
                });

        assertEquals("Логин не должен быть пустым и содержать пробелов.", exception.getMessage());
    }

    @Test
    public void addUserWithoutBirthdayTest() {
        User user = userController.getUser(1);
        user.setBirthday(null);

        final UserValidationException exception = assertThrows(
                UserValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.createUser(user);
                    }
                });

        assertEquals("Не передана дата рождения - Birthday = null", exception.getMessage());
    }

    @Test
    public void addUserFutureBirthdayTest() {
        User user = userController.getUser(1);
        user.setBirthday(LocalDate.of(2024, 8, 15));

        final UserValidationException exception = assertThrows(
                UserValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.createUser(user);
                    }
                });

        assertEquals("Некорректная дата рождения.", exception.getMessage());
    }

    @Test
    public void addUserWrongEmailSecondTest() {
        User user = userController.getUser(1);
        user.setEmail("");

        final UserValidationException exception = assertThrows(
                UserValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.createUser(user);
                    }
                });

        assertEquals("Передан пустой логин.", exception.getMessage());

        user.setEmail(" ");

        final UserValidationException exception1 = assertThrows(
                UserValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.createUser(user);
                    }
                });

        assertEquals("Передан пустой логин.", exception1.getMessage());
    }

    @Test
    public void updateUserTest() {
        User user = User.builder().build();
        user.setId(1);
        user.setName("Коля");
        user.setEmail("Kolay@yandex.ru");
        user.setLogin("1995Kolay1995");
        user.setBirthday(LocalDate.of(1996, 6, 16));
        userController.updateUser(user);

        List<User> users = userController.findAllUsers();
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
        User user = userController.getUser(1);
        user.setId(2);

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.updateUser(user);
                    }
                });

        assertEquals("Пользователь с id = \"2\" не найден", exception.getMessage());
    }
}

//    @Test
//    public void updateUserLoginWithSpaceTest() {
//        User user = userController.findAll().get(0);
//        user.setLogin("Nikolay 1995");
//        updateUserValidateException(user);
//    }
//
//    @Test
//    public void updateUserWithoutEmailTest() {
//        User user = userController.findAll().get(0);
//        user.setEmail("");
//        updateUserValidateException(user);
//
//        user.setEmail(" ");
//        updateUserValidateException(user);
//
//        user.setEmail("Nikolay@yandex.ru");
//    }
//
//    @Test
//    public void updateUserWithoutNameTest() {
//        User user = userController.findAll().get(0);
//        user.setLogin("Nikolay3000Ultra");
//        user.setName(" ");
//        userController.update(user);
//        assertEquals("Nikolay3000Ultra", user.getName());
//        user.setName("");
//        userController.update(user);
//        assertEquals("Nikolay3000Ultra", user.getName());
//    }
//
//    @Test
//    public void updateUserWithoutLoginTest() {
//        User user = userController.findAll().get(0);
//        user.setLogin(" ");
//        updateUserValidateException(user);
//        user.setLogin("");
//        updateUserValidateException(user);
//    }
//
//    @Test
//    public void updateUserFutureBirthdayTest() {
//        User user = userController.findAll().get(0);
//        user.setBirthday(LocalDate.of(2023, 12, 30));
//        userValidateException(user);
//    }
//
//    private void userValidateException(User user) {
//        final UserValidationException exception = assertThrows(
//                UserValidationException.class,
//                new Executable() {
//                    @Override
//                    public void execute() {
//                        userController.createUser(user);
//                    }
//                });
//        if (user == null) {
//            assertEquals("Пользователь отсутствует!", exception.getMessage());
//        } else {
//            assertEquals(MESS_USER_VALIDATE_EXCEPTION, exception.getMessage());
//        }
//
//    }
//
//    private void updateUserValidateException(User user) {
//        final UserValidationException exception = assertThrows(
//                UserValidationException.class,
//                new Executable() {
//                    @Override
//                    public void execute() {
//                        userController.update(user);
//                    }
//                });
//
//        assertEquals(MESS_USER_VALIDATE_EXCEPTION, exception.getMessage());
//    }
//}