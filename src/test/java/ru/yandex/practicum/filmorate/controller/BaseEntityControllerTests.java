package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.BaseEntity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public abstract class BaseEntityControllerTests<T extends BaseEntity> {
    private static final int port = 8080;

    @Autowired
    private MockMvc mockMvc;
    private static String baseUrl;
    public static String endPoint;
    public static Gson gson;

    T entity;

    @BeforeAll
    public static void initBeforeTest() {

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    @AfterEach
    void t() throws Exception {
        mockMvc.perform(delete(baseUrl))
                .andExpect(status().isOk());
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public void setEndpoint(String endpoint) {
        baseUrl = "http://localhost:" + port + endpoint;
        endPoint = endpoint;
    }

    @Test
    void addEntity() throws Exception {
        String jsonEntity = gson.toJson(entity);

        mockMvc.perform(post(baseUrl).content(jsonEntity)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void deleteEntity() throws Exception {
        String jsonEntity = gson.toJson(entity);

        mockMvc.perform(post(baseUrl)
                        .content(jsonEntity)
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
    void deleteGetEntityById() throws Exception {
        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isNotFound());

        String jsonEntity = gson.toJson(entity);

        mockMvc.perform(post(baseUrl)
                        .content(jsonEntity)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/1"))
                .andExpect(status().isOk());
    }
}
