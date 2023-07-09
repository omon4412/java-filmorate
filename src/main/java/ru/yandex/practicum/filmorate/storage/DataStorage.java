package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

/**
 * Общий интерфейс для хранилища данных
 *
 * @param <T> Тип данных
 */
public interface DataStorage<T> {
    T add(T obj);

    T update(T obj);

    T delete(T obj);

    int clearAll();

    Collection<T> getAllObjList();
}
