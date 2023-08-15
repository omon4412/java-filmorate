package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасывается, когда искомый рейтинг не существующет.
 */
public class MpaRatingNotFoundException extends RuntimeException {
    public MpaRatingNotFoundException(String message) {
        super(message);
    }
}
