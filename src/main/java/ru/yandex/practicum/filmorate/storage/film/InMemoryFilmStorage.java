package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int lastId = 0;

    protected int generateId() {
        return ++lastId;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.debug("Фильм добавлен - " + film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        log.debug("Фильм обновлён - " + film);
        return film;
    }

    @Override
    public int deleteFilm(Film film) {
        return -1;
    }

    @Override
    public Collection<Film> getAllFilmList() {
        log.debug("Количество фильмов - {}", films.size());
        return films.values();
    }

    @Override
    public Map<Integer, Film> getFilmsMap() {
        return films;
    }

    @Override
    public int clearFilms() {
        int count = films.size();
        films.clear();
        lastId = 0;
        log.debug("Фильмы очищены - " + count);
        return count;
    }

}
