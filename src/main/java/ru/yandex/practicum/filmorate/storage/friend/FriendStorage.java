package ru.yandex.practicum.filmorate.storage.friend;

import java.util.Set;

public interface FriendStorage {
    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Set<Integer> getUserFriendsIds(int userId);

    void removeAll();
}
