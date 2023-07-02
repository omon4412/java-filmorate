package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotExistException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    @Getter
    private final Map<Integer, Film> films = new HashMap<>();

    private int lastId = 0;

    @GetMapping
    public Collection<Film> getAllFilmList() {
        log.debug("Количество фильмов - {}", films.size());
        return films.values();
    }

    @DeleteMapping
    public void clearUsers() {
        films.clear();
        lastId = 0;
        log.debug("Фильмы очищены");
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film newFilm) throws FilmAlreadyExistException {
        if (films.values().stream().anyMatch(film -> film.getName().equals(newFilm.getName()))) {
            log.warn("Фильм с {} уже существует", newFilm.getName());
            throw new FilmAlreadyExistException(
                    "Фильм с указанным названием уже существует");
        }

        newFilm.setId(++lastId);
        films.put(newFilm.getId(), newFilm);
        log.debug("Фильм добавлен - " + newFilm);
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws FilmNotExistException {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с id {} не существует", film.getId());
            throw new FilmNotExistException(
                    "Фильма с указанным id не существует");
        }

        films.put(film.getId(), film);
        log.debug("Фильм обновлён - " + film);
        return film;
    }
}