package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendshipStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Override
    public void addFriend(int userId, int friendId) {
        Set<Integer> userFriends = friends.getOrDefault(userId, new HashSet<>());
        userFriends.add(friendId);
        friends.put(userId, userFriends);
        inMemoryUserStorage.getUserById(userId).setFriends(userFriends);
        if (friends.get(friendId) == null) {
            friends.put(friendId, new HashSet<>());
        }

        if (!friends.get(friendId).contains(userId)) {
            addFriend(friendId, userId);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        Set<Integer> userFriends = friends.get(userId);
        userFriends.remove(friendId);
        friends.put(userId, userFriends);
        inMemoryUserStorage.getUserById(userId).setFriends(userFriends);
        if (friends.get(friendId).contains(userId)) {
            removeFriend(friendId, userId);
        }
    }
}
