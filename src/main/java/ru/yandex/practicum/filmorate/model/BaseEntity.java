package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Абстрактный класс, представляющий базовую сущность с идентификатором и именем
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class BaseEntity {
    /**
     * Уникальный идентификатор сущности.
     */
    protected int id;

    /**
     * Имя сущности.
     */
    protected String name;
}

