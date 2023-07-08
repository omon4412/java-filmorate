package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    int deleteFilm(Film film);

    Collection<Film> getAllFilmList();

    Map<Integer,Film> getFilmsMap();

    int clearFilms();

}
