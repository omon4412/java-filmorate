package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {
    protected int id;

    @NonNull
    @NotBlank
    @Email
    protected String email;

    @NonNull
    @NotBlank
    protected String login;

    protected String name;

    @Past
    protected LocalDate birthday;
}
