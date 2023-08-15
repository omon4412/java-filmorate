package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

/**
 * Исключение, выбрасывается, когда были переданы неверные параметры.
 * <p>Примеры использования:
 * <pre>
 *     if (parameter == null) {
 *         throw new IncorrectParameterException("parameter");
 *     }
 *
 *     if (value &lt; 0 || value &gt; 100) {
 *         throw new IncorrectParameterException("Значение должно быть в диапазоне от 0 до 100", true);
 *     }
 * </pre>
 */
public class IncorrectParameterException extends RuntimeException {
    /**
     * Передаваемые данные об ошибке.
     */
    @Getter
    private final String parameter;
    /**
     * Флаг, указывающий на то, являются ли переданные данные сообщением.
     */
    @Getter
    private final boolean isMessage;

    /**
     * Конструктор исключения с заданным параметром.
     *
     * @param parameter некорректный параметр или аргумент, вызвавший исключение
     */
    public IncorrectParameterException(String parameter) {
        this.parameter = parameter;
        this.isMessage = false;
    }

    /**
     * Конструктор исключения с заданными параметром и флагом сообщения.
     *
     * @param parameter некорректный параметр или аргумент, вызвавший исключение
     * @param isMessage флаг, указывающий, что параметр являются ли переданные данные сообщением
     */
    public IncorrectParameterException(String parameter, boolean isMessage) {
        this.parameter = parameter;
        this.isMessage = isMessage;
    }
}
