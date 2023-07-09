package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private Map<Integer, Film> films = new HashMap<>();

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        pullFromStorage();
        if (films.values().stream().anyMatch(f -> f.getName().equals(film.getName()))) {
            log.warn("Фильм с {} уже существует", film.getName());
            throw new FilmAlreadyExistException("Фильм с " + film.getName() + " уже существует");
        }
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        pullFromStorage();
        checkFilmForExists(film.getId());
        return filmStorage.update(film);
    }

    public Film getFilmById(int id) {
        pullFromStorage();
        checkFilmForExists(id);
        return filmStorage.getFilmById(id);
    }

    private void checkFilmForExists(int id) {
        if (!films.containsKey(id)) {
            log.warn("Фильм с id {} не существует", id);
            throw new FilmNotFoundException(
                    "Фильма с id " + id + " не существует");
        }
    }

    private void pullFromStorage() {
        films = filmStorage.getFilmsMap();
    }

    public Collection<Film> getAllFilmList() {
        return filmStorage.getAllObjList();
    }

    public int clearFilms() {
        return filmStorage.clearAll();
    }
}
