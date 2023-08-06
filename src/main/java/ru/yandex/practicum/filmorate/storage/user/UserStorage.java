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
     * @param id - Идентификатор пользователя
     * @return Пользователь
     */
    User getUserById(int id);

    /**
     * Получить пользователя по email
     *
     * @param email - Почта
     * @return Пользователь
     */
    User getUserByEmail(String email);

    /**
     * Получить пользователя по login
     *
     * @param login - Логин
     * @return Пользователь
     */
    User getUserByLogin(String login);

    /**
     * Проверить пользователя на существование
     *
     * @param id - Идентификатор пользователя
     */
    boolean checkForExists(int id);
}
