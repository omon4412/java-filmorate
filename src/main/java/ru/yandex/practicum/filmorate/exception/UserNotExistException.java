package ru.yandex.practicum.filmorate.exception;

public class UserNotExistException extends Exception{
    public UserNotExistException(String message){
        super(message);
    }
}
