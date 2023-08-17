package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.validation.LocalDateDeserializer;
import ru.yandex.practicum.filmorate.validation.LocalDateSerializer;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс, представляющий модель пользователя.
 */
@Data
@RequiredArgsConstructor
public class User {
    /**
     * Идентификатор пользователя.
     */
    protected int id = -1;

    /**
     * Электронная почта пользователя.
     */
    @NonNull
    @NotBlank
    @Email(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    protected String email;

    /**
     * Логин пользователя.
     */
    @NonNull
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    protected String login;

    /**
     * Имя пользователя.
     */
    protected String name;

    /**
     * День рождения пользователя.
     */
    @Past
    @NonNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    protected LocalDate birthday;

    /**
     * Множество идентификаторов друзей пользователя.
     */
    protected Set<Integer> friendIds = new HashSet<>();

    /**
     * Конструктор копирования.
     *
     * @param other Пользователь, которого нужно скопировать
     */
    public User(User other) {
        this.id = other.id;
        this.email = other.email;
        this.login = other.login;
        this.name = other.name;
        this.birthday = other.birthday;
        this.friendIds = new HashSet<>(other.friendIds);
    }
}
