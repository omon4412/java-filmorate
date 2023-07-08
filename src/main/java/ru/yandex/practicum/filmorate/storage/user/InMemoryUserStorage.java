package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int lastId = 0;

    @Override
    public User addUser(User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.warn("Пользователь с {} уже существует", user.getEmail());
            throw new UserAlreadyExistException(
                    "Пользователь с " + user.getEmail() + " уже существует");
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.debug("Пользователь добавлен - " + user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователя с id {} не существует", user.getId());
            throw new UserNotExistException(
                    "Пользователя с id " + user.getId() + " не существует");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("Пользователь обновлён - " + user);
        return user;
    }

    @Override
    public User deleteUser(User user) {
        return null;
    }

    @Override
    public void clearUsers() {
        users.clear();
        lastId = 0;
        log.debug("Пользователи очищены");
    }

    @Override
    public Collection<User> getAllUserList() {
        log.debug("Пользователей - {}", users.size());
        return users.values();
    }

    protected int generateId() {
        return ++lastId;
    }
}
