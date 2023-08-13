package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class GenreControllerTests extends BaseEntityControllerTests<Genre> {

    @BeforeEach
    public void setEntity() {
        Genre testGenre = new Genre("Test");
        super.setEntity(testGenre);
    }

    @BeforeEach
    public void setEndpoint() {
        super.setEndpoint("/genres");
    }
}
