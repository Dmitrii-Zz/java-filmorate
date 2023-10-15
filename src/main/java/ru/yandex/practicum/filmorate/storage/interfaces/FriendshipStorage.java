package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.Set;

public interface FriendshipStorage {

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);
}
