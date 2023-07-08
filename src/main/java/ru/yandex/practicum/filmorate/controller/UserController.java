package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @DeleteMapping
    public void clearUsers() {

    }

    @GetMapping
    public Collection<User> getAllUserList() {
        return null;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) throws UserAlreadyExistException {
        return null;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws UserNotExistException {
        return null;
    }
}
