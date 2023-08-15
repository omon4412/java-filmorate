package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасывается, когда искомый жанр не существующет.
 */
public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(String message) {
        super(message);
    }
}
