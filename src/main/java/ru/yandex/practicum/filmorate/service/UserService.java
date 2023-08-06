package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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

    private final FriendStorage friendStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    /**
     * Добавляет пользователя в хранилище
     *
     * @param user Пользователь {@link User}
     * @return Добавленный пользователь
     * @throws UserAlreadyExistException Если пользователь уже существует
     */
    @Transactional
    public User addUser(User user) {
        if (userStorage.getUserByEmail(user.getEmail()) != null) {
            log.error("Пользователь с почтой {} уже существует", user.getEmail());
            throw new UserAlreadyExistException(
                    "Пользователь с почтой " + user.getEmail() + " уже существует");
        }
        if (userStorage.getUserByLogin(user.getLogin()) != null) {
            log.error("Пользователь с {} уже существует", user.getLogin());
            throw new UserAlreadyExistException(
                    "Пользователь с " + user.getLogin() + " уже существует");
        }
        ///Если имя не передано, то на место имени ставится логин
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
    @Transactional
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
    @Transactional
    public User deleteUser(int userId) {
        checkUserForExists(userId);
        for (int item : friendStorage.getUserFriendsIds(userId)) {
            friendStorage.removeFriend(userId, item);
        }
        return userStorage.delete(userId);
    }

    /**
     * Возвращает пользователя по его id
     *
     * @param id id пользователя
     * @return Найденный пользователь
     */
    @Transactional
    public User getUserById(int id) {
        checkUserForExists(id);
        User user = userStorage.getUserById(id);
        user.setFriendIds(friendStorage.getUserFriendsIds(id));
        return user;
    }

    /**
     * Возвращает список всех пользователей
     *
     * @return Список всех пользователей
     */
    public Collection<User> getAllUserList() {
        Collection<User> users = userStorage.getAllObjList();
        users.forEach(u -> u.setFriendIds(friendStorage.getUserFriendsIds(u.getId())));
        return users;
    }

    /**
     * Удаляет всех пользователей из хранилища
     *
     * @return Количество удалённых пользователей
     */
    @Transactional
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
    @Transactional
    public User updateFriendship(int userId, int friendId, boolean isAddFriend) {
        if (userId == friendId) {
            log.error("Нельзя " + (isAddFriend ? "добавить" : "удалить") + " самого себя");
            throw new IncorrectParameterException(
                    "Нельзя " + (isAddFriend ? "добавить" : "удалить") + " самого себя", true);
        }
        checkUserForExists(userId);
        checkUserForExists(friendId);

        User user = userStorage.getUserById(userId);

        if (isAddFriend) {
            friendStorage.addFriend(userId, friendId);
            log.debug("Пользователь id={} добавил в друзья с id={}", userId, friendId);
        } else {
            friendStorage.removeFriend(userId, friendId);
            log.debug("Пользователь id={} удалил из друзей id={}", userId, friendId);
        }
        user.setFriendIds(friendStorage.getUserFriendsIds(userId));
        return user;
    }

    /**
     * Возвращает список всех друзей пользователя
     *
     * @param userId id пользователя
     * @return Список друзей
     */
    @Transactional
    public List<User> getUsersFriends(int userId) {
        checkUserForExists(userId);
        Set<Integer> friendsIds = friendStorage.getUserFriendsIds(userId);
        return friendsIds.stream()
                .map(this::getUserById).collect(Collectors.toList());
    }

    /**
     * Возвращает список общих друзей у двух пользователей
     *
     * @param userId   Id пользователя
     * @param friendId Id друга
     * @return Список общих друзей
     * @throws IncorrectParameterException Если friendId == isAddFriend
     */
    @Transactional
    public List<User> getCommonFriends(int userId, int friendId) {
        if (userId == friendId) {
            throw new IncorrectParameterException(
                    "Нельзя получить список общих друзей у самого себя", true);
        }
        checkUserForExists(userId);
        checkUserForExists(friendId);

        List<Integer> usersFriends = new ArrayList<>(friendStorage.getUserFriendsIds(userId));
        List<Integer> friendsFriends = new ArrayList<>(friendStorage.getUserFriendsIds(friendId));

        usersFriends.removeIf(f -> f == friendId);
        friendsFriends.removeIf(f -> f == userId);

        usersFriends.retainAll(friendsFriends);

        return usersFriends.stream()
                .mapToInt(id -> id)
                .mapToObj(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    /**
     * Подтвеждает дружбу у пользователя, которому прислали предложение
     *
     * @param userId   Id пользователя
     * @param friendId Id друга
     * @return Пользователь
     */
    public User confirmFriendship(int userId, int friendId) {
        if (userId == friendId) {
            throw new IncorrectParameterException(
                    "Нельзя подтвердить дружбу у самого себя", true);
        }
        checkUserForExists(userId);
        checkUserForExists(friendId);

        User user = userStorage.getUserById(userId);
        friendStorage.confirmFriendship(userId, friendId);
        user.setFriendIds(friendStorage.getUserFriendsIds(userId));

        return user;
    }

    /**
     * Проверка на существование пользователя
     *
     * @param id id Пользователя
     * @throws UserNotFoundException Если пользователь не найден
     */
    private void checkUserForExists(int id) throws UserNotFoundException {
        if (!userStorage.checkForExists(id)) {
            log.error("Пользователя с id={} не существует", id);
            throw new UserNotFoundException(
                    "Пользователя с id=" + id + " не существует");
        }
    }
}
