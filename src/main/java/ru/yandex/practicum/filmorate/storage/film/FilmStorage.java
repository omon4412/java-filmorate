package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.util.Map;

/**
 * Интерфейс, имплементируеммый от {@link DataStorage}
 */
public interface FilmStorage extends DataStorage<Film> {
    /**
     * Получить мапу с фильмами
     *
     * @return Мапа с фильмами
     */
    Map<Integer, Film> getFilmsMap();

    /**
     * Получить фильм по id
     *
     * @return Фильм
     */
    Film getFilmById(int id);
}
