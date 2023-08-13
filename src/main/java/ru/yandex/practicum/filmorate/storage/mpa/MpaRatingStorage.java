package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.DataStorage;

public interface MpaRatingStorage extends DataStorage<MpaRating> {
    MpaRating getMpaRatingById(int id);
}
