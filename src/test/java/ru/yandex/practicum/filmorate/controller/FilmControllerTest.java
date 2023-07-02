package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.LocalDateAdapter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FilmControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private static final HttpHeaders headers = new HttpHeaders();
    @Autowired
    private MockMvc mockMvc;
    private String baseUrl;
    public String endPoint = "/films";

    public static Film testFilm;

    public static Gson gson;

    @BeforeEach
    public void initBeforeTest() {
        baseUrl = "http://localhost:" + port;
        testFilm = new Film("Test film", LocalDate.now().minusMonths(1));
        testFilm.setDuration(100);
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .serializeNulls()
                .create();
        headers.setContentType(MediaType.APPLICATION_JSON);

    }

    @AfterEach
    void clearFilms() throws Exception {
        mockMvc.perform(delete(baseUrl + endPoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
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
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> testFilm.setName(null));

        assertEquals("name is marked non-null but is null", exception.getMessage());
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

    @Test
    public void shouldGet3Films() throws Exception {
        String jsonFilm = gson.toJson(testFilm);

        mockMvc.perform(post(baseUrl + endPoint)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        testFilm.setName("Film 1");
        jsonFilm = gson.toJson(testFilm);
        mockMvc.perform(post(baseUrl + endPoint)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        testFilm.setName("Film 2");
        jsonFilm = gson.toJson(testFilm);
        mockMvc.perform(post(baseUrl + endPoint)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var result = mockMvc.perform(get(baseUrl + endPoint)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonElement jsonElement = JsonParser.parseString(result);
        var jsonArray = jsonElement.getAsJsonArray();
        assertEquals(3, jsonArray.size());
    }

    @Test
    public void shouldUpdateFilm() throws Exception {
        String jsonFilm = gson.toJson(testFilm);

        mockMvc.perform(post(baseUrl + endPoint)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Film film = new Film("New name", LocalDate.now().minusMonths(10));
        film.setId(1);
        film.setDuration(12);
        jsonFilm = gson.toJson(film);

        var result = mockMvc.perform(put(baseUrl + endPoint).content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().
                getResponse().getContentAsString();

        JsonElement jsonElement = JsonParser.parseString(result);
        Film returnedFilm = gson.fromJson(jsonElement, Film.class);
        assertEquals(film.getName(), returnedFilm.getName());
    }

    @Test
    public void shouldNotUpdateUserWithWrongId() throws Exception {
        testFilm.setId(456456456);
        String jsonUser = gson.toJson(testFilm);

        mockMvc.perform(put(baseUrl + endPoint).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}