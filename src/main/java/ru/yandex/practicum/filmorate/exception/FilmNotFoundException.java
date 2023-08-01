package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасывается, когда искомый фильм не существующет
 * <p>
 * Пример использования:
 * <pre>
 *     if (films.get(1)) {
 *         throw new FilmNotFoundException("Фильма не существует");
 *     }
 * </pre>
 */
public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(String message) {
        super(message);
    }
}
