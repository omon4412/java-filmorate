package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private Map<Integer, User> users = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        pullFromStorage();
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.warn("Пользователь с {} уже существует", user.getEmail());
            throw new UserAlreadyExistException(
                    "Пользователь с " + user.getEmail() + " уже существует");
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }

        return userStorage.add(user);
    }

    public User updateUser(User user) {
        pullFromStorage();
        checkUserForExists(user.getId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    public User getUserById(int id) {
        pullFromStorage();
        checkUserForExists(id);
        return userStorage.getUserById(id);
    }

    private void checkUserForExists(int id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователя с id {} не существует", id);
            throw new UserNotFoundException(
                    "Пользователя с id " + id + " не существует");
        }
    }

    private void pullFromStorage() {
        users = userStorage.getUsersMap();
    }

    public Collection<User> getAllUserList() {
        return userStorage.getAllObjList();
    }

    public int clearUsers() {
        return userStorage.clearAll();
    }
}
