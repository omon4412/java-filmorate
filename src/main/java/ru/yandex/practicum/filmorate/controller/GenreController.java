package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

/**
 * Контроллер для работы с жанрами.
 * Обрабатывает HTTP-запросы, связанные с жанрами
 */
@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController extends BaseEntityController<Genre> {
    @Autowired
    public GenreController(GenreService genreService) {
        super(genreService);
    }
}
