package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.util.IdConverter;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * Контроллер для работы с фильмами
 * Обрабатывает HTTP-запросы, связанные с фильмами.
 */
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Возвращает список всех фильмов
     *
     * @return Список всех фильмов
     */
    @GetMapping
    public Collection<Film> getAllFilmList() {
        return filmService.getAllFilmList();
    }

    /**
     * Удаляет все фильмы
     *
     * @return Количество удалённых фильмов
     */
    @DeleteMapping
    public int clearFilms() {
        return filmService.clearFilms();
    }

    /**
     * Создаёт фильм
     *
     * @param film Фильм {@link Film} для создания
     * @return Созданный фильм {@link Film}
     */
    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        return filmService.addFilm(film);
    }

    /**
     * Удаляет фильм по id
     *
     * @param filmId Id фильма
     * @return Удалённый фильм {@link Film}
     */
    @DeleteMapping("/{filmId}")
    public Film deleteFilmById(@PathVariable String filmId) {
        int filmIdInt = IdConverter.getIdInt(filmId);
        return filmService.deleteFilm(filmIdInt);
    }

    /**
     * Взвращает фильм по id
     *
     * @param filmId Id фильма
     * @return Фильм {@link Film}
     */
    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable String filmId) {
        int filmIdInt = IdConverter.getIdInt(filmId);
        return filmService.getFilmById(filmIdInt);
    }

    /**
     * Обновляет фильм по id
     *
     * @param film Фильм {@link Film} для обновления
     * @return Обновлённый фильм {@link Film}
     */
    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmService.updateFilm(film);
    }

    /**
     * Ставит лайк фильму
     *
     * @param filmId Id фильма
     * @param userId Id пользователя
     * @return Обновлённый фильм {@link Film}
     */
    @PutMapping("/{filmId}/like/{userId}")
    public Film addLikeToFilm(@PathVariable String filmId, @PathVariable String userId) {
        int filmIdInt = IdConverter.getIdInt(filmId);
        int userIdInt = IdConverter.getIdInt(userId);
        return filmService.updateLikeToFilm(filmIdInt, userIdInt, true);
    }

    /**
     * Убирает лайк у фильма
     *
     * @param filmId Id фильма
     * @param userId Id пользователя
     * @return Обновлённый фильм {@link Film}
     */
    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLikeFromFilm(@PathVariable String filmId, @PathVariable String userId) {
        int filmIdInt = IdConverter.getIdInt(filmId);
        int userIdInt = IdConverter.getIdInt(userId);
        return filmService.updateLikeToFilm(filmIdInt, userIdInt, false);
    }

    /**
     * Возвращает количество лайков у фильма
     *
     * @param filmId Id фильма
     * @return Количество лайков
     */
    @GetMapping("/{filmId}/like")
    public int getFilmLikesCount(@PathVariable String filmId) {
        int filmIdInt = IdConverter.getIdInt(filmId);
        return filmService.getFilmLikesCount(filmIdInt);
    }

    /**
     * Возвращает список популярных фильмов
     * в порядке убывания лайков
     *
     * @param count Количество фильмов в списке
     * @return Список популярных фильмов
     */
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
}