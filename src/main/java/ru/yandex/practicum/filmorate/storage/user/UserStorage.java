package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.util.Map;

/**
 * Интерфейс, имплементируеммый от {@link DataStorage}
 */
public interface UserStorage extends DataStorage<User> {
    Map<Integer, User> getUsersMap();

    User getUserById(int id);
}
