package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.util.IdConverter;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * Контроллер для работы с пользователями
 * Обрабатывает HTTP-запросы, связанные с фильмами.
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Удаляет всех пользователей
     *
     * @return Количество удалённых пользователей
     */
    @DeleteMapping
    public int clearUsers() {
        return userService.clearUsers();
    }

    /**
     * Возвращает список всех пользователей
     *
     * @return Список всех пользователей
     */
    @GetMapping
    public Collection<User> getAllUserList() {
        return userService.getAllUserList();
    }

    /**
     * Создаёт пользователя
     *
     * @param user Пользователь {@link User} для создания
     * @return Созданный пользователь {@link User}
     */
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    /**
     * Взвращает пользователя по id
     *
     * @param userId Id пользователя
     * @return Пользователь {@link User}
     */
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable String userId) {
        int userIdInt = IdConverter.getIdInt(userId);
        return userService.getUserById(userIdInt);
    }

    /**
     * Обновляет пользователя по id
     *
     * @param user Пользователь {@link User} для обновления
     * @return Обновлённый пользователь {@link User}
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * Удаляет пользователя по id
     *
     * @param userId Id пользователя
     * @return Удалённый пользователь {@link User}
     */
    @DeleteMapping("/{userId}")
    public User deleteUser(@PathVariable String userId) {
        int userIdInt = IdConverter.getIdInt(userId);
        return userService.deleteUser(userIdInt);
    }

    /**
     * Добавляет друга пользователю
     *
     * @param userId   Id пользователя
     * @param friendId Id друга
     * @return Обновлённый пользователь {@link User}
     */
    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriendToUser(@PathVariable String userId, @PathVariable String friendId) {
        int userIdInt = IdConverter.getIdInt(userId);
        int friendIdInt = IdConverter.getIdInt(friendId);
        return userService.updateFriendship(userIdInt, friendIdInt, true);
    }

    /**
     * Удалает друга у пользователя
     *
     * @param userId   Id пользователя
     * @param friendId Id друга
     * @return Обновлённый пользователь {@link User}
     */
    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriendFromUser(@PathVariable String userId, @PathVariable String friendId) {
        int userIdInt = IdConverter.getIdInt(userId);
        int friendIdInt = IdConverter.getIdInt(friendId);
        return userService.updateFriendship(userIdInt, friendIdInt, false);
    }

    /**
     * Возвращает список друзей пользователя
     *
     * @param userId Id пользователя
     * @return Список друзей
     */
    @GetMapping("/{userId}/friends")
    public List<User> getUsersFriends(@PathVariable String userId) {
        int userIdInt = IdConverter.getIdInt(userId);
        return userService.getUsersFriends(userIdInt);
    }

    /**
     * Возвращает список общих друзей у двух пользователей
     *
     * @param userId   Id пользователя
     * @param friendId Id друга
     * @return Список общих друзей
     */
    @GetMapping("/{userId}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable String userId, @PathVariable String friendId) {
        int userIdInt = IdConverter.getIdInt(userId);
        int friendIdInt = IdConverter.getIdInt(friendId);
        return userService.getCommonFriends(userIdInt, friendIdInt);
    }

    /**
     * Удалает друга у пользователя
     *
     * @param userId   Id пользователя
     * @param friendId Id друга
     * @return Обновлённый пользователь {@link User}
     */
    @PatchMapping("/{userId}/friends/{friendId}")
    public User confirmFriendship(@PathVariable String userId, @PathVariable String friendId) {
        int userIdInt = IdConverter.getIdInt(userId);
        int friendIdInt = IdConverter.getIdInt(friendId);
        return userService.confirmFriendship(userIdInt, friendIdInt);
    }
}
