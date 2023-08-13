package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.util.Constants;
import ru.yandex.practicum.filmorate.validation.LocalDateDeserializer;
import ru.yandex.practicum.filmorate.validation.LocalDateSerializer;
import ru.yandex.practicum.filmorate.validation.PastOrEqual;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс, представляющий модель фильма
 */
@Data
public class Film {
    /**
     * Идентификатор фильма
     */
    protected int id = -1;

    /**
     * Название фильма
     */
    @NonNull
    @NotBlank
    protected String name;

    /**
     * Описание фильма
     */
    @Size(max = 200)
    protected String description;

    /**
     * Дата выхода фильма
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @PastOrEqual(date = "1895-12-28", format = Constants.dataTimeFormat)
    @NonNull
    protected LocalDate releaseDate;

    /**
     * Продолжительность фильма
     */
    @Positive
    protected long duration;

    /**
     * Множество идентификаторов пользователей, которые оценили фильм
     */
    protected Set<Integer> userLikeIds = new HashSet<>();

    @NotNull
    protected MpaRating mpa;

    protected Set<Genre> genres = new HashSet<>();
}
