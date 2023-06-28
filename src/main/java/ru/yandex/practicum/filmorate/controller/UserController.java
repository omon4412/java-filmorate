package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int lastId = 0;

    @DeleteMapping
    public void clearUsers(){
        users.clear();
        lastId = 0;
        log.debug("Пользователи очищены");
    }

    @GetMapping
    public Collection<User> getAllUserList() {
        log.debug("Пользователей - {}", users.size());
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) throws UserAlreadyExistException {

        if (users.values().stream().anyMatch(user -> user.getEmail().equals(newUser.getEmail()))) {
            log.warn("Пользователь с {} уже существует", newUser.getEmail());
            throw new UserAlreadyExistException(
                    "Пользователь с указанным адресом электронной почты уже существует");
        }
        if (newUser.getName() == null || newUser.getName().equals("")) {
            newUser.setName(newUser.getLogin());
        }
        newUser.setId(++lastId);
        users.put(newUser.getId(), newUser);
        log.debug("Пользователь добавлен - " + newUser);
        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws UserNotExistException {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователя с {} не существует", user.getId());
            throw new UserNotExistException(
                    "Пользователя с указанным id не существует");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("Пользователь обновлён - " + user);
        return user;
    }
}
