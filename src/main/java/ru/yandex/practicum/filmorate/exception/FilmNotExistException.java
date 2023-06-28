package ru.yandex.practicum.filmorate.exception;

public class FilmNotExistException extends Exception{
    public FilmNotExistException(String message){
        super(message);
    }
}
