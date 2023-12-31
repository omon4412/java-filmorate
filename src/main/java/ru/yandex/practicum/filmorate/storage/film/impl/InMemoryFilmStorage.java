package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация {@link FilmStorage}.
 */
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    /**
     * Мапа для хранения фильмов.
     */
    private final Map<Integer, Film> films = new HashMap<>();
    /**
     * Последнее сгенерированное id.
     */
    private int lastId = 0;

    /**
     * Сгенерировать id.
     *
     * @return id
     */
    protected int generateId() {
        return ++lastId;
    }

    @Override
    public Film add(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.debug("Фильм добавлен - " + film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.debug("Фильм обновлён - " + film);
        return film;
    }

    @Override
    public Film delete(int id) {
        Film film = getFilmById(id);
        films.remove(film.getId());
        log.debug("Фильм удалён - " + film);
        return film;
    }

    @Override
    public Collection<Film> getAllObjList() {
        log.debug("Количество фильмов - {}", films.size());
        return films.values();
    }

    @Override
    public boolean checkForExists(int id) {
        return false;
    }

    @Override
    public Map<Integer, Film> getFilmsMap() {
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.get(id);
        log.debug("Получен фильм " + film);
        return film;
    }

    @Override
    public int clearAll() {
        int count = films.size();
        films.clear();
        lastId = 0;
        log.debug("Фильмы очищены - " + count);
        return count;
    }
}
