package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасывается, когда искомый пользователь не существующет
 * <p>
 * Пример использования:
 * <pre>
 *     if (users.get(1)) {
 *         throw new UserNotFoundException("Пользователя не существует");
 *     }
 * </pre>
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
