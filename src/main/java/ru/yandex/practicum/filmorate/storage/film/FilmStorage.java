package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.util.Map;

/**
 * Интерфейс, имплементируеммый от {@link DataStorage}
 */
public interface FilmStorage extends DataStorage<Film> {
    Map<Integer, Film> getFilmsMap();

    Film getFilmById(int id);
}
