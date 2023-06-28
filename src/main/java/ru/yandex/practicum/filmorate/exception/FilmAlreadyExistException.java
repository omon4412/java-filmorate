package ru.yandex.practicum.filmorate.exception;

public class FilmAlreadyExistException extends Exception{
    public FilmAlreadyExistException(String message) {
        super(message);
    }
}
