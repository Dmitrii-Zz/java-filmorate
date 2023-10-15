package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FeedStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.Instant;
import java.time.LocalDate;
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
        userValidate(user);
        validateName(user);
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        userValidate(user);
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
        Feed feed = new Feed();
        feed.setUserId(id);
        feed.setEntityId(friendId);
        feed.setTimestamp(Instant.now().toEpochMilli());
        feed.setEventType("FRIEND");
        feed.setOperation("ADD");
        feedRepository.addFeed(feed);
    }

    public void removeFriend(int id, int friendId) {
        validationIdUserAndOtherId(id, friendId);
        User user = userRepository.getUserById(id);
        User friend = userRepository.getUserById(friendId);
        user.getFriends().remove(friend.getId());
        friendshipRepository.removeFriend(id, friendId);
        Feed feed = new Feed();
        feed.setUserId(id);
        feed.setEntityId(friendId);
        feed.setTimestamp(Instant.now().toEpochMilli());
        feed.setEventType("FRIEND");
        feed.setOperation("REMOVE");
        feedRepository.addFeed(feed);
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

    private void userValidate(User user) {

        if (user == null) {
            throw new UserNotFoundException("В запросе отсутствует пользователь.");
        }

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

    private void validationIdUserAndOtherId(int id, int id1) {
        validationId(id);
        validationId(id1);

        if (id == id1) {
            throw new UserValidationException("Нельзя добавить или удалить себя из списка друзей.");
        }
    }
}
