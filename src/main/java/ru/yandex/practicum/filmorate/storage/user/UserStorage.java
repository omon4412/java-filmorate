package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.util.Map;

/**
 * Интерфейс, имплементируеммый от {@link DataStorage}
 */
public interface UserStorage extends DataStorage<User> {
    /**
     * Получить мапу с пользователями
     *
     * @return Мапа с пользователями
     */
    Map<Integer, User> getUsersMap();

    /**
     * Получить пользователя по id
     *
     * @return Пользователь
     */
    User getUserById(int id);
}
