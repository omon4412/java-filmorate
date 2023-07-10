package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

public class IncorrectParameterException extends RuntimeException {
    @Getter
    private final String parameter;
    @Getter
    private final boolean isMessage;

    public IncorrectParameterException(String parameter) {
        this.parameter = parameter;
        this.isMessage = false;
    }

    public IncorrectParameterException(String parameter, boolean isMessage) {
        this.parameter = parameter;
        this.isMessage = isMessage;
    }


}
