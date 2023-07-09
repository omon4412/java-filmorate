package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Модель хранения ошибок валидации Spring
 */
@Getter
@RequiredArgsConstructor

public class Violation {
    private final String fieldName;
    private final String errorMessage;
}
