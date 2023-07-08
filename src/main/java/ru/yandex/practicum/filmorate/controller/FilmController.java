package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    FilmController(FilmService filmService){
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
    public Film createFilm(@Valid @RequestBody Film film) throws FilmAlreadyExistException {
       return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws FilmNotExistException {
        return filmService.updateFilm(film);
    }
}