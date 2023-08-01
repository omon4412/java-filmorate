package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасывается, когда добавляемый пользователь уже существующет
 * <p>
 * Пример использования:
 * <pre>
 *     if (userAlreadyExists) {
 *         throw new UserAlreadyExistException("Пользователь уже существует");
 *     }
 * </pre>
 */
public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
