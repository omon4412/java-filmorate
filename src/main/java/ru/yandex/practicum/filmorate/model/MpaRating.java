package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

@Data
public class MpaRating {
    protected int id;

    @NonNull
    @NotBlank
    protected String name;
}
