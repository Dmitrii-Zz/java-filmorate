package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.impl.inMemory.InMemoryFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.impl.inMemory.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private final InMemoryFriendshipStorage inMemoryFriendshipStorage =
            new InMemoryFriendshipStorage(inMemoryUserStorage);
    private final UserService userService = new UserService(inMemoryUserStorage, inMemoryFriendshipStorage);
    private final UserController userController = new UserController(userService);

    @BeforeEach
    public void createUser() {
        User user = User.builder().build();
        user.setName("Николай");
        user.setEmail("Nikolay@yandex.ru");
        user.setLogin("Nikolay1995");
        user.setBirthday(LocalDate.of(1995, 5, 15));
        userController.createUser(user);

        User user2 = User.builder().build();
        user2.setName("Настя");
        user2.setEmail("Nastya@yandex.ru");
        user2.setLogin("Nastyha");
        user2.setBirthday(LocalDate.of(1998, 1, 30));
        userController.createUser(user2);

        User user3 = User.builder().build();
        user3.setName("Михалыч");
        user3.setEmail("Mihail@yandex.ru");
        user3.setLogin("Misha");
        user3.setBirthday(LocalDate.of(1997, 4, 1));
        userController.createUser(user3);
    }

    @Test
    public void createUserTest() {
        List<User> users = userController.findAllUsers();
        assertEquals(3, users.size());

        User user1 = users.get(0);

        assertAll("Сравнение полей пользователя.",
                () -> assertEquals(1, user1.getId()),
                () -> assertEquals("Николай", user1.getName()),
                () -> assertEquals("Nikolay@yandex.ru", user1.getEmail()),
                () -> assertEquals("Nikolay1995", user1.getLogin()),
                () -> assertEquals(LocalDate.of(1995, 5, 15), user1.getBirthday()),
                () -> assertEquals(0, user1.getFriends().size()));
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
        assertEquals(3, users.size());

        User user1 = users.get(0);

        assertAll("Сравнение полей пользователя.",
                () -> assertEquals(1, user1.getId()),
                () -> assertEquals("Коля", user1.getName()),
                () -> assertEquals("Kolay@yandex.ru", user1.getEmail()),
                () -> assertEquals("1995Kolay1995", user1.getLogin()),
                () -> assertEquals(LocalDate.of(1996, 6, 16), user1.getBirthday()));
    }

    @Test
    public void updateUserWrongIdTest() {
        User user = userController.getUser(1);
        user.setId(333);

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.updateUser(user);
                    }
                });

        assertEquals("Пользователь с id = \"333\" не найден", exception.getMessage());
    }

    @Test
    public void updateUserWrongId2Test() {
        User user = userController.getUser(1);
        user.setId(-1);

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.updateUser(user);
                    }
                });

        assertEquals("Передан неверный ИД пользователя - id = \"-1\" ", exception.getMessage());
    }

    @Test
    public void addFriendAndDeleteFriendTest() {
        userController.addFriend(1, 2);
        assertEquals(1, userController.getUser(1).getFriends().size());
        assertEquals(1, userController.getUser(2).getFriends().size());

        userController.deleteFriend(1, 2);
        assertEquals(0, userController.findAllFriends(1).size());
        assertEquals(0, userController.findAllFriends(2).size());
    }

    @Test
    public void addFriendYourselfTest() {
        final UserValidationException exception = assertThrows(
                UserValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.addFriend(1, 1);
                    }
                });

        assertEquals("Нельзя добавить или удалить себя из списка друзей.", exception.getMessage());
    }

    @Test
    public void getAllFriendTest() {
        userController.addFriend(1, 2);
        assertEquals(1, userController.findAllFriends(1).size());
    }

    @Test
    public void findMutualFriendsTest() {
        userController.addFriend(1, 2);
        userController.addFriend(2, 3);

        assertEquals(2, userController.findMutualFriends(1, 3).get(0).getId());
    }
}
