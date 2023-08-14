package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.util.DebugOnly;

import java.util.Set;

public interface LikeStorage {
    /**
     * Добавить лайк фильму
     *
     * @param filmId id фильма
     * @param userId id пользователя
     */
    void addLikeToFilm(int filmId, int userId);

    /**
     * Удалить лайк у фильма
     *
     * @param filmId id фильма
     * @param userId id пользователя
     */
    void removeLikeFromFilm(int filmId, int userId);

    /**
     * Получить список идентификаторов пользователей
     * лайкнувших фильм
     *
     * @param filmId id фильма
     * @return Список идентификаторов пользователей
     */
    Set<Integer> getUsersLikesIds(int filmId);

    /**
     * Получить количество лайков у фильма
     *
     * @param filmId id фильма
     * @return Количество лайков
     */
    int getLikesCount(int filmId);

    /**
     * ТОЛЬКО ДЛЯ ОТЛАДКИ
     * Удалить все связи между друзьями
     */
    @DebugOnly("Только для отладки!")
    void removeAll();
}
