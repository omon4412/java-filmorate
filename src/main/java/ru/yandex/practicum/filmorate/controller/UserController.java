package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    UserController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping
    public int clearUsers() {
        return userService.clearUsers();
    }

    @GetMapping
    public Collection<User> getAllUserList() {
        return userService.getAllUserList();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable String userId) {
        int userIdInt = getUserIdInt(userId);
        return userService.getUserById(userIdInt);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public User deleteUser(@PathVariable String userId) {
        int userIdInt = getUserIdInt(userId);
        return userService.deleteUser(userIdInt);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriendToUser(@PathVariable String userId, @PathVariable String friendId) {
        int userIdInt = getUserIdInt(userId);
        int friendIdInt = getUserIdInt(friendId);
        return userService.addFriendToUser(userIdInt, friendIdInt);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriendFromUser(@PathVariable String userId, @PathVariable String friendId) {
        int userIdInt = getUserIdInt(userId);
        int friendIdInt = getUserIdInt(friendId);
        return userService.deleteFriendFromUser(userIdInt, friendIdInt);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getUsersFriends(@PathVariable String userId) {
        int userIdInt = getUserIdInt(userId);
        return userService.getUsersFriends(userIdInt);
    }

    /**
     * Преобразование строки id пользователя в число
     *
     * @param userId id пользователя
     * @return id пользователя в формате числа
     */
    private int getUserIdInt(String userId) {
        int userIdInt;
        try {
            userIdInt = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            throw new IncorrectParameterException("userId");
        }
        return userIdInt;
    }
}
