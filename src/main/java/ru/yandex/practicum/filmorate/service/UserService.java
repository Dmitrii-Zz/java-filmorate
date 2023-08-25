package ru.yandex.practicum.filmorate.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public void addFriend(User user, User friend) {
        user.getFriends().add(friend.getId());
        if (!friend.getFriends().contains(user.getId())) {
            addFriend(friend, user);
        }
    }

    public void removeFriend(User user, User friend) {
        user.getFriends().remove(friend.getId());
        if (friend.getFriends().contains(user.getId())) {
            removeFriend(friend, user);
        }
    }

    public List<User> findMutualFriends(int id, int otherId) {

        List<User> mutualFriends = new ArrayList<>();
        User user = inMemoryUserStorage.getUserById(id);
        User otherUser = inMemoryUserStorage.getUserById(otherId);

        for (Integer i : user.getFriends()) {
            if (otherUser.getFriends().contains(i)) {
                mutualFriends.add(inMemoryUserStorage.getUserById(i));
            }
        }

        return mutualFriends;
    }

    public List<User> getFriends(int id) {

        User user = inMemoryUserStorage.getUserById(id);
        List<User> friends = new ArrayList<>();

        for (Integer i : user.getFriends()) {
            friends.add(inMemoryUserStorage.getUserById(i));
        }

        return friends;
    }
}
