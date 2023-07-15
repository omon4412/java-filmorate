package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;

public class IdConverter {
    private IdConverter() {
    }

    /**
     * Преобразование строки id в числовое значение
     *
     * @param id id пользователя в формате строки
     * @return id пользователя в формате числа
     */
    public static int getIdInt(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new IncorrectParameterException("id");
        }
    }
}
