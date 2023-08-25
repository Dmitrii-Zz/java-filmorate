//package ru.yandex.practicum.filmorate.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class UserServiceTest {
//    private static UserService userService;
//    private static User user;
//    private static User user2;
//    private static User user3;
//    private static User friend;
//
//    @BeforeEach
//    public void createUser() {
//
//        user = User.builder()
//                .id(1)
//                .email("Petrovich@outlook.com")
//                .name("Евгений Петрович")
//                .login("Petrovich")
//                .birthday(LocalDate.of(1960, 9, 15))
//                .build();
//
//        user2 = User.builder()
//                .id(3)
//                .email("Victoria@yandex.ru")
//                .name("Викусик")
//                .login("Vica")
//                .birthday(LocalDate.of(1970, 4, 1))
//                .build();
//
//        user3 = User.builder()
//                .id(3)
//                .email("Borisov@mail.ru")
//                .name("Костя")
//                .login("Kostik")
//                .birthday(LocalDate.of(1975, 7, 28))
//                .build();
//
//        friend = User.builder()
//                .id(2)
//                .email("Vasiliy1960@yandex.ru")
//                .name("Михалыч")
//                .login("Miha")
//                .birthday(LocalDate.of(1961, 1, 15))
//                .build();
//    }
//
//    @Test
//    public void addFriendTest() {
//
//        assertEquals(0,userService.findAllFriends(user).size());
//        assertEquals(0,userService.findAllFriends(friend).size());
//
//        userService.addFriend(user, friend);
//
//        assertEquals(1,userService.findAllFriends(user).size());
//        assertEquals(1,userService.findAllFriends(friend).size());
//    }
//
//    @Test
//    public void removeFriendTest() {
//
//        userService.addFriend(user, friend);
//
//        assertEquals(1,userService.findAllFriends(user).size());
//        assertEquals(1,userService.findAllFriends(friend).size());
//
//        userService.removeFriend(user, friend);
//
//        assertEquals(0,userService.findAllFriends(user).size());
//        assertEquals(0,userService.findAllFriends(friend).size());
//    }
//
//    @Test
//    public void findMutualFriendsTest() {
//        assertAll("Друзей у всех 0",
//                () -> assertEquals(0, userService.findAllFriends(user).size()),
//                () -> assertEquals(0, userService.findAllFriends(user2).size()),
//                () -> assertEquals(0, userService.findAllFriends(user3).size()),
//                () -> assertEquals(0, userService.findAllFriends(friend).size()));
//
//        userService.addFriend(user, friend);
//        userService.addFriend(user2, friend);
//        userService.addFriend(user2, user3);
//
//        assertAll("У двух пользователей по 1 другу, у двух других по 2 друга",
//                () -> assertEquals(1, userService.findAllFriends(user).size()),
//                () -> assertEquals(2, userService.findAllFriends(user2).size()),
//                () -> assertEquals(1, userService.findAllFriends(user3).size()),
//                () -> assertEquals(2, userService.findAllFriends(friend).size()));
//
//
//        List<User> mutualFriend = userService.findMutualFriends(user, user2);
//
//        for (User u : mutualFriend) {
//            assertEquals("Михалыч", u.getName());
//        }
//    }
//}
