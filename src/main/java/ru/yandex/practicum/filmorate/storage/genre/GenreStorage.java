package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DataStorage;
import ru.yandex.practicum.filmorate.util.DebugOnly;

import java.util.Set;

public interface GenreStorage extends DataStorage<Genre> {
    Genre getGenreById(int id);

    /**
     * Добавить жанр к к фильму
     *
     * @param filmId  id пользователя
     * @param genreId id друга
     */
    void addGenreToFilm(int filmId, int genreId);

    /**
     * Удалить жанр у фильма
     *
     * @param filmId  id фильма
     * @param genreId id жанра
     */
    void removeGenreFromFilm(int filmId, int genreId);

    /**
     * Получить список жанров фильма
     *
     * @param filmId id фильма
     * @return Список жанров фильма
     */
    Set<Genre> getFilmGenres(int filmId);

    /**
     * ТОЛЬКО ДЛЯ ОТЛАДКИ
     * Удалить все связи между фильмом и жанрами
     */
    @DebugOnly("Только для отладки!")
    void removeAll();
}
