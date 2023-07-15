package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация {@link UserStorage}
 */
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int lastId = 0;

    @Override
    public User add(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        log.debug("Пользователь добавлен - " + user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.debug("Пользователь обновлён - " + user);
        return user;
    }

    @Override
    public User delete(User user) {
        log.debug("Пользователь удалён - " + user);
        users.remove(user.getId());
        return user;
    }

    @Override
    public int clearAll() {
        int count = users.size();
        users.clear();
        lastId = 0;
        log.debug("Пользователи очищены");
        return count;
    }

    @Override
    public Collection<User> getAllObjList() {
        log.debug("Пользователей - {}", users.size());
        return users.values();
    }

    protected int generateId() {
        return ++lastId;
    }

    @Override
    public Map<Integer, User> getUsersMap() {
        return users;
    }

    @Override
    public User getUserById(int id) {
        var user = users.get(id);
        log.debug("Получен пользователь " + user);
        return user;
    }
}
