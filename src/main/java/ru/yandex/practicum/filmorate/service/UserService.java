package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.interfaces.FeedStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.filmorate.Constants.CORRECT_ID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userRepository;
    private final FriendshipStorage friendshipRepository;
    private final FeedStorage feedRepository;

    public User getUser(int id) {
        validationId(id);
        return userRepository.getUserById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        validateUserLogin(user);
        validateName(user);
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        validateUserLogin(user);
        validationId(user.getId());
        validateName(user);
        return userRepository.update(user);
    }

    public void addFriend(int id, int friendId) {
        validationIdUserAndOtherId(id, friendId);
        User user = userRepository.getUserById(id);
        User friend = userRepository.getUserById(friendId);
        user.getFriends().add(friend.getId());
        friendshipRepository.addFriend(id, friendId);
        feedRepository.addFeed(id, friendId, EventType.FRIEND, Operation.ADD);
    }

    public void removeFriend(int id, int friendId) {
        validationIdUserAndOtherId(id, friendId);
        User user = userRepository.getUserById(id);
        User friend = userRepository.getUserById(friendId);
        user.getFriends().remove(friend.getId());
        friendshipRepository.removeFriend(id, friendId);
        feedRepository.addFeed(id, friendId, EventType.FRIEND, Operation.REMOVE);
    }

    public List<User> findMutualFriends(int id, int otherId) {
        validationIdUserAndOtherId(id, otherId);
        List<User> mutualFriends = new ArrayList<>();
        User user = userRepository.getUserById(id);
        User otherUser = userRepository.getUserById(otherId);

        for (Integer i : user.getFriends()) {
            if (otherUser.getFriends().contains(i)) {
                mutualFriends.add(userRepository.getUserById(i));
            }
        }

        return mutualFriends;
    }

    public List<User> getFriends(int id) {
        validationId(id);
        User user = userRepository.getUserById(id);
        List<User> friends = new ArrayList<>();

        for (Integer i : user.getFriends()) {
            friends.add(userRepository.getUserById(i));
        }

        return friends;
    }

    public List<Feed> getUserFeeds(int id) {
        validationId(id);
        return feedRepository.getUserFeeds(id);
    }

    public void deleteUserById(int userId) {
        validationId(userId);
        userRepository.deleteUserById(userId);
    }

    private void validationId(int id) {
        if (id < CORRECT_ID) {
            throw new UserNotFoundException(String.format("Передан неверный ИД пользователя - id = \"%d\" ", id));
        }

        if (!(userRepository.findUserId(id))) {
            throw new UserNotFoundException(String.format("Пользователь с id = \"%d\" не найден", id));
        }
    }

    private void validateUserLogin(User user) {
        if (user.getLogin().contains(" ")) {
            throw new UserValidationException("Логин не должен содержать пробелов.");
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

    private void validationIdUserAndOtherId(int id, int id1) {
        validationId(id);
        validationId(id1);

        if (id == id1) {
            throw new UserValidationException("Нельзя добавить или удалить себя из списка друзей.");
        }
    }

    public List<Film> getFilmsRecomendation(int id) {
        validationId(id);
        return userRepository.getFilmsRecomendation(id);
    }
}