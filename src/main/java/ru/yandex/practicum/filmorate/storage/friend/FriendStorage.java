package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.util.DebugOnly;

import java.util.Set;

/**
 * Интерфейс для управдения друзьями
 */
public interface FriendStorage {
    /**
     * Добавить пользователя в друзья
     *
     * @param userId   id пользователя
     * @param friendId id друга
     */
    void addFriend(int userId, int friendId);

    /**
     * Удалить пользователя из друзей
     *
     * @param userId   id пользователя
     * @param friendId id друга
     */
    void removeFriend(int userId, int friendId);

    /**
     * Получить список идентификаторов друзей
     *
     * @param userId id пользователя
     * @return Список идентификаторов друзей
     */
    Set<Integer> getUserFriendsIds(int userId);

    /**
     * Подвердить дружбу
     *
     * @param userId   id пользователя
     * @param friendId id друга
     */
    void confirmFriendship(int userId, int friendId);

    /**
     * ТОЛЬКО ДЛЯ ОТЛАДКИ
     * Удалить все связи между друзьями
     */
    @DebugOnly("Только для отладки!")
    void removeAll();
}
