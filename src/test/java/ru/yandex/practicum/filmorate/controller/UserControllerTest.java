package ru.yandex.practicum.filmorate.controller;

import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.LocalDateAdapter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void shouldNotAddUser_IfEmailIsExists() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
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
        testUser.setLogin("login45345");
        jsonUser = gson.toJson(testUser);
        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        testUser.setEmail("2@test.ru");
        testUser.setLogin("login898545");
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

        User user = new User("newMail@go1og.ru", testUser.getLogin(), LocalDate.now().minusMonths(10));
        user.setId(1);
        jsonUser = gson.toJson(user);

        var result = mockMvc.perform(put(baseUrl).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();

        JsonElement jsonElement = JsonParser.parseString(result);
        User returnedUser = gson.fromJson(jsonElement, User.class);
        assertEquals(testUser.getLogin(), returnedUser.getLogin());
        assertEquals("newMail@go1og.ru", returnedUser.getEmail());
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
    public void shouldNotGetUser_IfIdLetter() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/give2user"))
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

    @Test
    public void shouldDeleteUser() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(delete(baseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotDeleteUser_IfIdIsWord() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(delete(baseUrl + "/user1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotDeleteUser_IfIdIncorrect() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(delete(baseUrl + "/5656656"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteUserFromFriendsList() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        testUser.setEmail("some@gg.ru");
        testUser.setLogin("login45345");
        jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(put(baseUrl + "/1/friends/2"))
                .andExpect(status().isOk());

        mockMvc.perform(delete(baseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isNotFound());

        var result = mockMvc.perform(get(baseUrl + "/2/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
        assertEquals(0, jsonArray.size());
    }

    @Test
    public void shouldAddFriendToUser() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        testUser.setEmail("some@gg.ru");
        testUser.setLogin("login45345");
        jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/2"))
                .andExpect(status().isOk());

        mockMvc.perform(put(baseUrl + "/1/friends/2"))
                .andExpect(status().isOk());

        var result = mockMvc.perform(get(baseUrl + "/1/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
        var user = jsonArray.get(0).getAsJsonObject();
        assertEquals(1, jsonArray.size());
        assertEquals(2, user.get("id").getAsInt());

        result = mockMvc.perform(get(baseUrl + "/2/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        jsonArray = JsonParser.parseString(result).getAsJsonArray();
        assertEquals(0, jsonArray.size());
    }

    @Test
    public void shouldAddFriendToUser_AndConfirm() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        testUser.setEmail("some@gg.ru");
        testUser.setLogin("login45345");
        jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/2"))
                .andExpect(status().isOk());

        mockMvc.perform(put(baseUrl + "/1/friends/2"))
                .andExpect(status().isOk());

        var result = mockMvc.perform(get(baseUrl + "/1/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
        var user = jsonArray.get(0).getAsJsonObject();
        assertEquals(1, jsonArray.size());
        assertEquals(2, user.get("id").getAsInt());

        result = mockMvc.perform(get(baseUrl + "/2/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        jsonArray = JsonParser.parseString(result).getAsJsonArray();
        assertEquals(0, jsonArray.size());

        mockMvc.perform(patch(baseUrl + "/2/friends/1"))
                .andExpect(status().isOk());

        result = mockMvc.perform(get(baseUrl + "/2/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        jsonArray = JsonParser.parseString(result).getAsJsonArray();
        user = jsonArray.get(0).getAsJsonObject();
        assertEquals(1, jsonArray.size());
        assertEquals(1, user.get("id").getAsInt());
    }

    @Test
    public void shouldAddTwoFriendsToUser() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());

        testUser.setEmail("some@gg.ru");
        testUser.setLogin("login45345");
        jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/2"))
                .andExpect(status().isOk());

        testUser.setEmail("some3@gg.ru");
        testUser.setLogin("login4533335");
        jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/3"))
                .andExpect(status().isOk());

        mockMvc.perform(put(baseUrl + "/1/friends/2"))
                .andExpect(status().isOk());

        var result = mockMvc.perform(get(baseUrl + "/1/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
        var user = jsonArray.get(0).getAsJsonObject();
        assertEquals(1, jsonArray.size());
        assertEquals(2, user.get("id").getAsInt());

        mockMvc.perform(get(baseUrl + "/1/friends/common/3"))
                .andExpect(status().isOk());

        mockMvc.perform(put(baseUrl + "/1/friends/3"))
                .andExpect(status().isOk());

        result = mockMvc.perform(get(baseUrl + "/1/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        jsonArray = JsonParser.parseString(result).getAsJsonArray();
        user = jsonArray.get(0).getAsJsonObject();
        assertEquals(2, jsonArray.size());
        assertEquals(2, user.get("id").getAsInt());
    }

    @Test
    public void shouldNotAddFriendToUser_IfOneOfIdsNotExists() throws Exception {
        mockMvc.perform(put(baseUrl + "/1/friends/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotAddFriendToUser_IfOneOfIdsIsWord() throws Exception {
        mockMvc.perform(put(baseUrl + "/user/friends/friend"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAddFriendToUser_IfIdHimSelf() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(put(baseUrl + "/1/friends/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteFriendToUser() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());

        testUser.setEmail("some@gg.ru");
        testUser.setLogin("login45345");
        jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/2"))
                .andExpect(status().isOk());

        mockMvc.perform(put(baseUrl + "/1/friends/2"))
                .andExpect(status().isOk());

        var result = mockMvc.perform(get(baseUrl + "/1/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
        var user = jsonArray.get(0).getAsJsonObject();
        assertEquals(1, jsonArray.size());
        assertEquals(2, user.get("id").getAsInt());

        result = mockMvc.perform(get(baseUrl + "/2/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        jsonArray = JsonParser.parseString(result).getAsJsonArray();
        assertEquals(0, jsonArray.size());

        mockMvc.perform(delete(baseUrl + "/1/friends/2"))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/2"))
                .andExpect(status().isOk());

        result = mockMvc.perform(get(baseUrl + "/1/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        jsonArray = JsonParser.parseString(result).getAsJsonArray();
        assertEquals(0, jsonArray.size());

        result = mockMvc.perform(get(baseUrl + "/2/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        jsonArray = JsonParser.parseString(result).getAsJsonArray();
        assertEquals(0, jsonArray.size());
    }

    @Test
    public void shouldNotDeleteFriendToUser_IfOneOfIdsNotExists() throws Exception {
        mockMvc.perform(put(baseUrl + "/1/friends/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotDeleteFriendToUser_IfOneOfIdsIsWord() throws Exception {
        mockMvc.perform(put(baseUrl + "/user/friends/friend"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldGetCommonFriends() throws Exception {
        String jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());

        testUser.setEmail("some@gg.ru");
        testUser.setLogin("login45345");
        jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/2"))
                .andExpect(status().isOk());

        mockMvc.perform(put(baseUrl + "/1/friends/2"))
                .andExpect(status().isOk());

        var result = mockMvc.perform(get(baseUrl + "/1/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
        var user = jsonArray.get(0).getAsJsonObject();
        assertEquals(1, jsonArray.size());
        assertEquals(2, user.get("id").getAsInt());

        result = mockMvc.perform(get(baseUrl + "/2/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        jsonArray = JsonParser.parseString(result).getAsJsonArray();
        assertEquals(0, jsonArray.size());

        testUser.setEmail("some3@gg.ru");
        testUser.setLogin("login4534sdsfd5");
        jsonUser = gson.toJson(testUser);

        mockMvc.perform(post(baseUrl)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/3"))
                .andExpect(status().isOk());

        mockMvc.perform(put(baseUrl + "/3/friends/2"))
                .andExpect(status().isOk());

        result = mockMvc.perform(get(baseUrl + "/3/friends"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        jsonArray = JsonParser.parseString(result).getAsJsonArray();
        user = jsonArray.get(0).getAsJsonObject();
        assertEquals(1, jsonArray.size());
        assertEquals(2, user.get("id").getAsInt());

        result = mockMvc.perform(get(baseUrl + "/3/friends/common/1"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        jsonArray = JsonParser.parseString(result).getAsJsonArray();
        user = jsonArray.get(0).getAsJsonObject();
        assertEquals(1, jsonArray.size());
        assertEquals(2, user.get("id").getAsInt());
    }
}