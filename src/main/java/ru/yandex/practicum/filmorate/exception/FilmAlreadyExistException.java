package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)

public class FilmAlreadyExistException extends Exception {
    public FilmAlreadyExistException(String message) {
        super(message);
    }
}
