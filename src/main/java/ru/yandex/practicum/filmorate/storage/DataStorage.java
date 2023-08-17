package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

/**
 * Общий интерфейс для хранилища данных.
 *
 * @param <T> Тип данных
 */
public interface DataStorage<T> {
    /**
     * Добавляет объект в хранилище.
     *
     * @param obj Объект
     * @return Добавленный объект
     */
    T add(T obj);

    /**
     * Обновляет объект в хранилище.
     *
     * @param obj Объект
     * @return Обновлённый объект
     */
    T update(T obj);

    /**
     * Удаляет объект в хранилище.
     *
     * @param id Идентификатор объекта
     * @return Удалённый объект
     */
    T delete(int id);

    /**
     * Очищает хранилище.
     *
     * @return Количество очищенных объектов
     */
    int clearAll();

    /**
     * Возвращает список всех объектов их хранилища.
     *
     * @return Список объектов
     */
    Collection<T> getAllObjList();

    boolean checkForExists(int id);
}
