package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.MpaRating;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MpaRatingControllerTests extends BaseEntityControllerTests<MpaRating> {

    @BeforeEach
    public void setEntity() {
        MpaRating testRating = new MpaRating(-1, "Test");
        super.setEntity(testRating);
    }

    @BeforeEach
    public void setEndpoint() {
        super.setEndpoint("/mpa");
    }
}
