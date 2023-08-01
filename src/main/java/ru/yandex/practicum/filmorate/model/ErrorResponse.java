package ru.yandex.practicum.filmorate.model;

/**
 * Модель объекта ошибки для возврата клиенту
 */
public class ErrorResponse {
    /**
     * Текст ошибки
     */
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}