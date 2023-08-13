package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

/**
 * Контроллер для работы с рейтингом фильмов
 * Обрабатывает HTTP-запросы, связанные с рейтингом
 */
@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaRatingController extends BaseEntityController<MpaRating> {
    @Autowired
    public MpaRatingController(MpaRatingService mpaRatingService) {
        super(mpaRatingService);
    }
}
