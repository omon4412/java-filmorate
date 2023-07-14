package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы с пользователями
 */
@Service
@Slf4j
public class UserService {
    /**
     * Хранилище пользователей
     */
    private final UserStorage userStorage;
    /**
     * Мапа для хранения пользователей по их id
     */
    private Map<Integer, User> users = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Добавляет пользователя в хранилище
     *
     * @param user Пользователь {@link User}
     * @return Добавленный пользователь
     * @throws UserAlreadyExistException Если пользователь уже существует
     */
    public User addUser(User user) {
        pullFromStorage();
        if (users.values().stream()
                .anyMatch(u -> u.getEmail()
                        .equals(user.getEmail()))) {
            log.error("Пользователь с {} уже существует", user.getEmail());
            throw new UserAlreadyExistException(
                    "Пользователь с " + user.getEmail() + " уже существует");
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }

        return userStorage.add(user);
    }

    /**
     * Обновляет пользователя в хранилище
     *
     * @param user Пользователь {@link User}
     * @return Обновлённый пользователь
     */
    public User updateUser(User user) {
        checkUserForExists(user.getId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    /**
     * Удаляет пользователя из хранилища
     *
     * @param userId id пользователя
     * @return Удалённый пользователь
     */
    public User deleteUser(int userId) {
        pullFromStorage();
        checkUserForExists(userId);

        User user = userStorage.getUserById(userId);
        Set<Integer> friendsIds = user.getFriendIds();

        friendsIds.stream()
                .map(userStorage::getUserById)
                .forEach(friend -> friend.getFriendIds()
                        .remove(userId));
        return userStorage.delete(user);
    }

    /**
     * Возвращает пользователя по его id
     *
     * @param id id пользователя
     * @return Найденный пользователь
     */
    public User getUserById(int id) {
        checkUserForExists(id);
        return userStorage.getUserById(id);
    }

    /**
     * Возвращает список всех пользователей
     *
     * @return Список всех пользователей
     */
    public Collection<User> getAllUserList() {
        List<User> sortUsers = new ArrayList<>(userStorage.getAllObjList());
        sortUsers.sort(Comparator.comparing(User::getId));
        return sortUsers;
    }

    /**
     * Удаляет все пользователей из хранилища
     *
     * @return Количество удалённых пользователей
     */
    public int clearUsers() {
        return userStorage.clearAll();
    }

    /**
     * Обновляет статус друга у пользователя
     *
     * @param userId      id пользователя
     * @param friendId    id друга
     * @param isAddFriend Флаг, указывающий, добавить или удалить друга
     * @return Обновлённый пользователь
     * @throws IncorrectParameterException Если friendId == isAddFriend
     */
    public User updateFriendship(int userId, int friendId, boolean isAddFriend) {
        if (userId == friendId) {
            log.error("Нельзя " + (isAddFriend ? "добавить" : "удалить") + " самого себя");
            throw new IncorrectParameterException(
                    "Нельзя " + (isAddFriend ? "добавить" : "удалить") + " самого себя", true);
        }
        checkUserForExists(userId);
        checkUserForExists(friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (isAddFriend) {
            user.getFriendIds().add(friend.getId());
            friend.getFriendIds().add(user.getId());
            log.debug("Пользователь id={} добавил в друзья с id={}", userId, friendId);
        } else {
            user.getFriendIds().remove(friend.getId());
            friend.getFriendIds().remove(user.getId());
            log.debug("Пользователь id={} удалил из друзей id={}", userId, friendId);
        }

        userStorage.update(friend);
        return userStorage.update(user);
    }

    /**
     * Возвращает список всех друзей пользователя
     *
     * @param userId id пользователя
     * @return Список друзей
     */
    public List<User> getUsersFriends(int userId) {
        checkUserForExists(userId);
        User user = userStorage.getUserById(userId);
        Set<Integer> friendsIds = user.getFriendIds();
        return friendsIds.stream()
                .map(userStorage::getUserById)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список общих друзей у двух пользователей
     *
     * @param userId   Id пользователя
     * @param friendId Id друга
     * @return Список общих друзей
     * @throws IncorrectParameterException Если friendId == isAddFriend
     */
    public List<User> getCommonFriends(int userId, int friendId) {
        if (userId == friendId) {
            throw new IncorrectParameterException(
                    "Нельзя получить список общих друзей у самого себя", true);
        }
        checkUserForExists(userId);
        checkUserForExists(friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        List<Integer> usersFriends = new ArrayList<>(user.getFriendIds());
        List<Integer> friendsFriends = new ArrayList<>(friend.getFriendIds());

        usersFriends.removeIf(f -> f == friendId);
        friendsFriends.removeIf(f -> f == userId);

        usersFriends.retainAll(friendsFriends);

        return usersFriends.stream()
                .mapToInt(id -> id)
                .mapToObj(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    /**
     * Получение всех пользователей из хранилища
     */
    private void pullFromStorage() {
        users = userStorage.getUsersMap();
    }

    /**
     * Проверка на существование пользователя
     *
     * @param id id Пользователя
     * @throws UserNotFoundException Если пользователь не найден
     */
    private void checkUserForExists(int id) throws UserNotFoundException {
        pullFromStorage();
        if (!users.containsKey(id)) {
            log.error("Пользователя с id={} не существует", id);
            throw new UserNotFoundException(
                    "Пользователя с id=" + id + " не существует");
        }
    }
}
