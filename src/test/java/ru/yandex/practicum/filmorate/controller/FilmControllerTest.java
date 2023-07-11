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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.LocalDateAdapter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private String filmBaseUrl;
    private String userBaseUrl;
    public String endPoint = "/films";
    public static Film testFilm;
    public static Gson gson;

    @BeforeEach
    public void initBeforeTest() {
        filmBaseUrl = "http://localhost:" + port + endPoint;
        userBaseUrl = "http://localhost:" + port + "/users";
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
        mockMvc.perform(delete(filmBaseUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @AfterEach
    void clearUsers() throws Exception {
        mockMvc.perform(delete(userBaseUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAddNewFilm() {
        String jsonFilm = gson.toJson(testFilm);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(filmBaseUrl,
                HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Film addedFilm = gson.fromJson(response.getBody(), Film.class);
        assertEquals(1, addedFilm.getId());
    }

    @Test
    public void shouldNotAddFilm_WithoutName() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> testFilm.setName(null));

        assertEquals("name is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void shouldNotAddFilm_WithEmptyName() {
        testFilm.setName("");
        String jsonFilm = gson.toJson(testFilm);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(filmBaseUrl,
                HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldNotAddFilm_WithNegativeDuration() {
        testFilm.setDuration(-100);
        String jsonFilm = gson.toJson(testFilm);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(filmBaseUrl,
                HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldNotAddFilm_WithLongDescription() {
        String fishText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Sed tempor nisl nec justo faucibus, in consequat felis condimentum. " +
                "Curabitur id metus lobortis, eleifend enim nec, fringilla leo. Suspendisse scelerisqu.";
        testFilm.setDescription(fishText);
        String jsonFilm = gson.toJson(testFilm);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(filmBaseUrl,
                HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldNotAddFilm_WithWrongDate() {
        testFilm.setReleaseDate(LocalDate.parse("1700-01-01"));
        String jsonFilm = gson.toJson(testFilm);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(filmBaseUrl,
                HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldGet3Films() throws Exception {
        String jsonFilm = gson.toJson(testFilm);

        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        testFilm.setName("Film 1");
        jsonFilm = gson.toJson(testFilm);
        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        testFilm.setName("Film 2");
        jsonFilm = gson.toJson(testFilm);
        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var result = mockMvc.perform(get(filmBaseUrl)
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

        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Film film = new Film("New name", LocalDate.now().minusMonths(10));
        film.setId(1);
        film.setDuration(12);
        jsonFilm = gson.toJson(film);

        var result = mockMvc.perform(put(filmBaseUrl).content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().
                getResponse().getContentAsString();

        JsonElement jsonElement = JsonParser.parseString(result);
        Film returnedFilm = gson.fromJson(jsonElement, Film.class);
        assertEquals(film.getName(), returnedFilm.getName());
    }

    @Test
    public void shouldNotUpdateFilm_WithWrongId() throws Exception {
        testFilm.setId(456456456);
        String jsonfilm = gson.toJson(testFilm);

        mockMvc.perform(put(filmBaseUrl).content(jsonfilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldGetFilm() throws Exception {
        String jsonfilm = gson.toJson(testFilm);

        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonfilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var film = mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();

        JsonElement jsonElement = JsonParser.parseString(film);
        Film returnedFilm = gson.fromJson(jsonElement, Film.class);
        testFilm.setId(1);
        assertEquals(testFilm, returnedFilm);
    }

    @Test
    public void shouldNotGetFilm_IfIdIsWrong() throws Exception {
        String jsonFilm = gson.toJson(testFilm);

        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(filmBaseUrl + "/654656"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteFilm() throws Exception {
        String jsonFilm = gson.toJson(testFilm);

        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(delete(filmBaseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotDeleteFilm_IfIdIsWord() throws Exception {
        String jsonFilm = gson.toJson(testFilm);

        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(delete(filmBaseUrl + "/rrrrr"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotDeleteFilm_IfIdIncorrect() throws Exception {
        String jsonFilm = gson.toJson(testFilm);

        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(delete(filmBaseUrl + "/5656656"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAddLikeToFilm() throws Exception {
        String jsonFilm = gson.toJson(testFilm);

        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user = new User("email@bb.bb", "login", LocalDate.now().minusMonths(10));
        String jsonUser = gson.toJson(user);

        mockMvc.perform(post(userBaseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(userBaseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(put(filmBaseUrl + "/1/like/1"))
                .andExpect(status().isOk());

        var result = mockMvc.perform(get(filmBaseUrl + "/1/like"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        int likesCount = JsonParser.parseString(result).getAsInt();
        assertEquals(1, likesCount);

        result = mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonElement film = JsonParser.parseString(result).getAsJsonObject();
        assertEquals(1, gson.fromJson(film, Film.class).getUserLikeIds().size());
    }

    @Test
    public void shouldDeleteLikeToFilm() throws Exception {
        String jsonFilm = gson.toJson(testFilm);

        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user = new User("email@bb.bb", "login", LocalDate.now().minusMonths(10));
        String jsonUser = gson.toJson(user);

        mockMvc.perform(post(userBaseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(userBaseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(put(filmBaseUrl + "/1/like/1"))
                .andExpect(status().isOk());

        var result = mockMvc.perform(get(filmBaseUrl + "/1/like"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        int likesCount = JsonParser.parseString(result).getAsInt();
        assertEquals(1, likesCount);

        result = mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonElement film = JsonParser.parseString(result).getAsJsonObject();
        assertEquals(1, gson.fromJson(film, Film.class).getUserLikeIds().size());


        mockMvc.perform(delete(filmBaseUrl + "/1/like/1"))
                .andExpect(status().isOk());

        result = mockMvc.perform(get(filmBaseUrl + "/1/like"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        likesCount = JsonParser.parseString(result).getAsInt();
        assertEquals(0, likesCount);

        result = mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        film = JsonParser.parseString(result).getAsJsonObject();
        assertEquals(0, gson.fromJson(film, Film.class).getUserLikeIds().size());
    }

    @Test
    public void shouldAdd2LikesToFilm() throws Exception {
        String jsonFilm = gson.toJson(testFilm);

        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user = new User("email@bb.bb", "login", LocalDate.now().minusMonths(10));
        String jsonUser = gson.toJson(user);

        mockMvc.perform(post(userBaseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        user = new User("email2@bb.bb", "login2", LocalDate.now().minusMonths(10));
        jsonUser = gson.toJson(user);

        mockMvc.perform(post(userBaseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(userBaseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(userBaseUrl + "/2"))
                .andExpect(status().isOk());

        mockMvc.perform(put(filmBaseUrl + "/1/like/1"))
                .andExpect(status().isOk());

        mockMvc.perform(put(filmBaseUrl + "/1/like/2"))
                .andExpect(status().isOk());

        var result = mockMvc.perform(get(filmBaseUrl + "/1/like"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        int likesCount = JsonParser.parseString(result).getAsInt();
        assertEquals(2, likesCount);

        result = mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonElement film = JsonParser.parseString(result).getAsJsonObject();
        assertEquals(2, gson.fromJson(film, Film.class).getUserLikeIds().size());
    }

    @Test
    public void shouldNotAddLikeToFilm_IfIdIsWord() throws Exception {
        String jsonFilm = gson.toJson(testFilm);

        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(put(filmBaseUrl + "/1/like/word"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put(filmBaseUrl + "/word/like/1d"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotDeleteLikeToFilm_IfIdIncorrect() throws Exception {
        String jsonFilm = gson.toJson(testFilm);

        mockMvc.perform(post(filmBaseUrl)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(delete(filmBaseUrl + "/1/like/word"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete(filmBaseUrl + "/word/like/1d"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(filmBaseUrl + "/1"))
                .andExpect(status().isOk());
    }
}