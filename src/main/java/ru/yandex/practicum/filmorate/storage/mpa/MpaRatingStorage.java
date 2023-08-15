package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.DataStorage;

/**
 * Интерфейс, представляющий хранилище рейтингов фильмов.
 */
public interface MpaRatingStorage extends DataStorage<MpaRating> {
    /**
     * Получить рейтинг по id.
     *
     * @param id Идентификатор рейтинга
     * @return Рейтинг
     */
    MpaRating getMpaRatingById(int id);
}
