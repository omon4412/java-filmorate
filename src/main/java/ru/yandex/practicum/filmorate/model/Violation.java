package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Модель хранения ошибок валидации Spring
 */
@Getter
@RequiredArgsConstructor
@ToString

public class Violation {
    /**
     * Название поля
     */
    private final String fieldName;
    /**
     * Текст ошибки
     */
    private final String errorMessage;
}
