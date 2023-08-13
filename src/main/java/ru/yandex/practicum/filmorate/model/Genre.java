package ru.yandex.practicum.filmorate.model;

public class Genre extends BaseEntity {
    public Genre(int id, String name) {
        super(id, name);
    }

    public Genre(String name) {
        super(name);
    }
}
