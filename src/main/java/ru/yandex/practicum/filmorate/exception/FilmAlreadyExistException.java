package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасывается, когда добавляемый фильм уже существующет.
 * <p>
 * Пример использования:
 * <pre>
 *     if (filmAlreadyExists) {
 *         throw new FilmAlreadyExistException("Фильм с таким названием уже существует");
 *     }
 * </pre>
 */
public class FilmAlreadyExistException extends RuntimeException {
    public FilmAlreadyExistException(String message) {
        super(message);
    }
}
