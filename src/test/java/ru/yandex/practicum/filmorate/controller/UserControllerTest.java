package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.LocalDateAdapter;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {
    private static final int port = 8080;

    @Autowired
    private MockMvc mockMvc;
    private String baseUrl;
    public String endPoint = "/users";
    public static User testUser;
    public static Gson gson;

    @BeforeEach
    public void initBeforeTest() {
        baseUrl = "http://localhost:" + port + endPoint;
        testUser = new User("admin@email.test", "admin",
                LocalDate.now().minusMonths(10));
        testUser.setBirthday(LocalDate.ofYearDay(2000, 256));

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .serializeNulls()
                .create();
    }

    @AfterEach
    void clearUsers() throws Exception {
        mockMvc.perform(delete(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddNewUser() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    public void shouldAddUser_WithoutName() throws Exception {
        testUser.setName(null);
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("admin"));
    }

    @Test
    public void shouldAddUser_WithEmptyName() throws Exception {
        testUser.setName("");
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("admin"));
    }

    @Test
    public void shouldNotAddUser_WithWrongLogin_1() throws Exception {
        testUser.setLogin("Testlogin--=");
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAddUser_WithWrongLogin_2() throws Exception {
        testUser.setLogin("#$2dfs%");
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAddUser_WithWrongLogin_3() throws Exception {
        testUser.setLogin("dff dsfsd");
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAddUser_WithWrongEmail_1() throws Exception {
        testUser.setEmail("wrong@@@");
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAddUser_WithWrongEmail_2() throws Exception {
        testUser.setEmail("1@1.1");
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAddUser_WithWrongEmail_3() throws Exception {
        testUser.setEmail("user-name@domain.com.");
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAddUser_WithWrongEmail_4() throws Exception {
        testUser.setEmail("username@.com");
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка на отсутсвие кириллицы в почте")
    public void shouldNotAddUser_WithWrongEmail_5() throws Exception {
        testUser.setEmail("пользователь@майл.com");
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAddUser_WithWrongDate() throws Exception {
        testUser.setBirthday(LocalDate.now().plusDays(1));
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldGet3Users() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        testUser.setEmail("1@test.ru");
        jsonUser = gson.toJson(testUser);
        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        testUser.setEmail("2@test.ru");
        jsonUser = gson.toJson(testUser);
        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var result = mockMvc.perform(get(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonElement jsonElement = JsonParser.parseString(result);
        var jsonArray = jsonElement.getAsJsonArray();
        assertEquals(3, jsonArray.size());
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user = new User("newMail@goog.ru", testUser.getLogin(), LocalDate.now().minusMonths(10));
        user.setId(1);
        jsonUser = gson.toJson(user);

        var result = mockMvc.perform(put(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().
                getResponse().getContentAsString();

        JsonElement jsonElement = JsonParser.parseString(result);
        User returnedUser = gson.fromJson(jsonElement, User.class);
        assertEquals(testUser.getLogin(), returnedUser.getLogin());
        assertEquals("newMail@goog.ru", returnedUser.getEmail());
    }

    @Test
    public void shouldNotUpdateUser_WithWrongId() throws Exception {
        testUser.setId(456456456);
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(put(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldGetUser() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var user = mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();

        JsonElement jsonElement = JsonParser.parseString(user);
        User returnedUser = gson.fromJson(jsonElement, User.class);
        testUser.setId(1);
        testUser.setName(testUser.getLogin());
        assertEquals(testUser, returnedUser);
    }

    @Test
    public void shouldNotGetUser_IfIdNegative() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/-1"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldNotGetUser_IfIdIsWrong() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/654656"))
                .andExpect(status().isNotFound());
    }
}