package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.PastOrEqual;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class Film {
    protected int id;

    @NotNull
    @NotBlank
    protected String name;

    @Size(max = 200)
    protected String description;

    @PastOrEqual(date = "1895-12-28", format = "yyyy-MM-dd")
    protected LocalDate releaseDate;

    @Positive
    protected long duration;
}
