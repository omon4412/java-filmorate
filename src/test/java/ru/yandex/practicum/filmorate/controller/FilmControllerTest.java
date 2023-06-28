package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.LocalDateAdapter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private static final HttpHeaders headers = new HttpHeaders();

    private String baseUrl;
    public String endPoint = "/films";

    public static Film testFilm;

    public static Gson gson;

    @BeforeEach
    public void initBeforeTest() {
        baseUrl = "http://localhost:" + port;
        testFilm = new Film();
        testFilm.setName("Test film");
        testFilm.setReleaseDate(LocalDate.now().minusMonths(1));
        testFilm.setDuration(100);
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .serializeNulls()
                .create();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void shouldAddNewFilm() {
        String jsonFilm = gson.toJson(testFilm);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + endPoint,
                HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Film addedFilm = gson.fromJson(response.getBody(), Film.class);
        assertEquals(1, addedFilm.getId());
    }

    @Test
    public void shouldNotAddFilmWithoutName() {
        testFilm.setName(null);

        String jsonFilm = gson.toJson(testFilm);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + endPoint,
                HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldNotAddFilmWithEmptyName() {
        testFilm.setName("");
        String jsonFilm = gson.toJson(testFilm);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + endPoint,
                HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldNotAddFilmWithNegativeDuration() {
        testFilm.setDuration(-100);
        String jsonFilm = gson.toJson(testFilm);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + endPoint,
                HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldNotAddFilmWithLongDescription() {
        String fishText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Sed tempor nisl nec justo faucibus, in consequat felis condimentum. " +
                "Curabitur id metus lobortis, eleifend enim nec, fringilla leo. Suspendisse scelerisqu.";
        testFilm.setDescription(fishText);
        String jsonFilm = gson.toJson(testFilm);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + endPoint,
                HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldNotAddFilmWithWrongDate() {
        testFilm.setReleaseDate(LocalDate.parse("1700-01-01"));
        String jsonFilm = gson.toJson(testFilm);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + endPoint,
                HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}