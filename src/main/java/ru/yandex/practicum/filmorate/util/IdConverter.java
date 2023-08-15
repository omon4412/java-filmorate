package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;

/**
 * Класс конвертер идентификатора.
 */
public class IdConverter {
    private IdConverter() {
    }

    /**
     * Преобразование строки id в числовое значение.
     *
     * @param id id пользователя в формате строки
     * @return id пользователя в формате числа
     * @throws IncorrectParameterException Когда id не число
     */
    public static int getIdInt(String id) throws IncorrectParameterException {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new IncorrectParameterException("id");
        }
    }
}
