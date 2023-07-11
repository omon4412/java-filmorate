package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilmList() {
        return filmService.getAllFilmList();
    }

    @DeleteMapping
    public int clearFilms() {
        return filmService.clearFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        return filmService.addFilm(film);
    }

    @DeleteMapping("/{filmId}")
    public Film deleteFilmById(@PathVariable String filmId) {
        int filmIdInt = getFilmIdInt(filmId);
        return filmService.deleteFilm(filmIdInt);
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable String filmId) {
        int filmIdInt = getFilmIdInt(filmId);
        return filmService.getFilmById(filmIdInt);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLikeToFilm(@PathVariable String filmId, @PathVariable String userId) {
        int filmIdInt = getFilmIdInt(filmId);
        int userIdInt = getFilmIdInt(userId);
        return filmService.updateLikeToFilm(filmIdInt, userIdInt, true);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLikeFromFilm(@PathVariable String filmId, @PathVariable String userId) {
        int filmIdInt = getFilmIdInt(filmId);
        int userIdInt = getFilmIdInt(userId);
        return filmService.updateLikeToFilm(filmIdInt, userIdInt, false);
    }

    @GetMapping("/{filmId}/like")
    public int getFilmLikesCount(@PathVariable String filmId) {
        int filmIdInt = getFilmIdInt(filmId);
        return filmService.getFilmLikesCount(filmIdInt);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) String count) {
        if (count == null) {
            return filmService.getPopularFilms(10);
        } else {
            try {
                int intCount = Integer.parseInt(count);
                if (intCount < 0) {
                    throw new IncorrectParameterException("count");
                }
                return filmService.getPopularFilms(intCount);
            } catch (NumberFormatException e) {
                throw new IncorrectParameterException("count");
            }
        }
    }

    private int getFilmIdInt(String filmId) {
        try {
            return Integer.parseInt(filmId);
        } catch (NumberFormatException e) {
            throw new IncorrectParameterException("filmId");
        }
    }
}