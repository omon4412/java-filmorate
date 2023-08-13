package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

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
    @NonNull
    @NotBlank
    protected String name;
}

