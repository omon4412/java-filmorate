package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.validation.PastOrEqual;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class Film {
    protected int id;

    @NonNull
    @NotBlank
    protected String name;

    @Size(max = 200)
    protected String description;

    @PastOrEqual(date = "1895-12-28", format = "yyyy-MM-dd")
    @NonNull
    protected LocalDate releaseDate;

    @Positive
    protected long duration;
}
