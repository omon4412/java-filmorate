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
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    protected int id;

    @NonNull
    @NotBlank
    protected String name;

    @Size(max = 200)
    protected String description;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @PastOrEqual(date = "1895-12-28", format = Constants.dataTimeFormat)
    @NonNull
    protected LocalDate releaseDate;

    @Positive
    protected long duration;

    protected Set<Integer> userLikeIds = new HashSet<>();
}
