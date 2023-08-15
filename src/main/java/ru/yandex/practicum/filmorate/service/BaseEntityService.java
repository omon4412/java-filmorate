package ru.yandex.practicum.filmorate.service;

import java.util.Collection;

/**
 * Интерфейс базового сервиса для работы с сущностями.
 *
 * @param <T> Тип сущности
 */
public interface BaseEntityService<T> {

    /**
     * Удалить сущность по идентификатору.
     *
     * @param id Идентификатор сущности
     * @return Удаленная сущность
     */
    T deleteEntity(int id);

    /**
     * Получить сущность по идентификатору.
     *
     * @param id Идентификатор сущности
     * @return Найденная сущность
     */
    T getEntityById(int id);

    /**
     * Получить список всех сущностей.
     *
     * @return Список всех сущностей
     */
    Collection<T> getAllEntities();

    /**
     * Создать новую сущность.
     *
     * @param entity Новая сущность
     * @return Созданная сущность
     */
    T addEntity(T entity);

    /**
     * Обновлить сущность.
     *
     * @param entity Сущность для обновления
     * @return Обновленная сущность
     */
    T updateEntity(T entity);

    /**
     * Удалить все сущности.
     *
     * @return Количество удаленных сущностей
     */
    int clearEntities();
}
