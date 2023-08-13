package ru.yandex.practicum.filmorate.model;

public class MpaRating extends BaseEntity {

    public MpaRating(int id, String name) {
        super(id, name);
    }

    public MpaRating(String name) {
        super(name);
    }
}
