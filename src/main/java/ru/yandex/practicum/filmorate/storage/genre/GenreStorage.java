package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DataStorage;

public interface GenreStorage extends DataStorage<Genre> {
    Genre getGenreById(int id);
}
