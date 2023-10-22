package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> findAllUsers() {
        log.info("Возврат списка всех пользователей.");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        log.info("Запрос на возврат пользователя id = " + id);
        return userService.getUser(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрос на создание пользователя.");
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление пользователя id = " + user.getId());
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрос от пользователя id = " + id + " на добавление друга id = " + friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрос от пользователя id = " + id + " на удаление друга id = " + friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable int id) {
        log.info(String.format("Возврат списка друзей от пользователя id = %d", id));
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info(String.format("Запрос от юзера id = %d на возврат списка общих друзей с id = %d", id, otherId));
        return userService.findMutualFriends(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getUserFeeds(@PathVariable int id) {
        log.info("Возврат ленты событий пользователя id = " + id);
        return userService.getUserFeeds(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getFilmsRecommendation(@PathVariable int id) {
        log.info(String.format("Запрос от юзера id = %d на список рекомендаций", id));
        return userService.getFilmsRecomendation(id);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable int userId) {
        log.info("Удаление пользователя id = " + userId);
        userService.deleteUserById(userId);
    }
}