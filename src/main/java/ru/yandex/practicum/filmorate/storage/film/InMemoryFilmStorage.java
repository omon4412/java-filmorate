package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация {@link FilmStorage}
 */
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int lastId = 0;

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
    public Film delete(Film film) {
        log.debug("Фильм удалён - " + film);
        films.remove(film.getId());
        return film;
    }

    @Override
    public Collection<Film> getAllObjList() {
        log.debug("Количество фильмов - {}", films.size());
        return films.values();
    }

    @Override
    public Map<Integer, Film> getFilmsMap() {
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        var film = films.get(id);
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
