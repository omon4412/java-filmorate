package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Violation;
import ru.yandex.practicum.filmorate.validation.LocalDateDeserializer;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Обработчик всех исключений
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    /**
     * Обработчик исключения {@link IncorrectParameterException}
     * Возникает когда были переданны входные параметры в неверном формате
     *
     * @param e Исключение {@link IncorrectParameterException}
     * @return Объект {@link ErrorResponse} c информацией об ошибке.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(final IncorrectParameterException e) {
        if (!e.isMessage()) {
            return new ErrorResponse(
                    String.format("Ошибка с полем \"%s\".", e.getParameter())
            );
        } else {
            return new ErrorResponse(e.getParameter());
        }
    }

    /**
     * Обработчик исключения {@link FilmNotFoundException}
     * Возникает когда искомый фильм не найден
     *
     * @param e Исключение {@link FilmNotFoundException}
     * @return Объект {@link ErrorResponse} c информацией об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFoundException(final FilmNotFoundException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    /**
     * Обработчик исключения {@link UserNotFoundException}
     * Возникает когда искомый пользователь не найден
     *
     * @param e Исключение {@link UserNotFoundException}
     * @return Объект {@link ErrorResponse} c информацией об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    /**
     * Обработчик исключения {@link UserAlreadyExistException}
     * Возникает когда пользователь уже существует
     *
     * @param e Исключение {@link UserAlreadyExistException}
     * @return Объект {@link ErrorResponse} c информацией об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExistException(final UserAlreadyExistException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    /**
     * Обработчик исключения {@link IncorrectParameterException}
     * Возникает когда внутри {@link LocalDateDeserializer} или {@link LocalDateDeserializer}
     * возникает исключение при парсинге {@link LocalDate}
     *
     * @param e Исключение {@link HttpMessageNotReadableException}
     * @return Объект {@link ErrorResponse} c информацией об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleLocalDateParseErrorException(final HttpMessageNotReadableException e) {
        Throwable localDateParseError = e.getCause().getCause();
        if (localDateParseError instanceof IncorrectParameterException) {
            IncorrectParameterException error = (IncorrectParameterException) localDateParseError;
            return new ErrorResponse(error.getParameter());
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * Обработчик исключения {@link FilmAlreadyExistException}
     * Возникает когда фильм уже существует
     *
     * @param e Исключение {@link FilmAlreadyExistException}
     * @return Объект {@link ErrorResponse} c информацией об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserIsExistException(final FilmAlreadyExistException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    /**
     * Обработчик исключения {@link ConstraintViolationException}
     * Возникает когда действие нарушает ограничение на структуру модели
     *
     * @param e Исключение {@link ConstraintViolationException}
     * @return Спискок всех нарушений {@link Violation}
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Violation> onConstraintValidationException(
            ConstraintViolationException e
    ) {
        List<Violation> collect = e.getConstraintViolations().stream()
                .map(violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());
        log.warn(collect.toString());
        return collect;
    }

    /**
     * Обработчик исключения {@link MethodArgumentNotValidException}
     * Возникает когда проверка аргумента с аннотацией @Valid не удалась
     *
     * @param e Исключение {@link MethodArgumentNotValidException}
     * @return Спискок всех нарушений {@link Violation}
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Violation> onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        List<Violation> collect = e.getBindingResult().getFieldErrors().stream()
                .map(violation -> new Violation(violation.getField(), violation.getDefaultMessage()))
                .collect(Collectors.toList());
        log.error(collect.toString());
        return collect;
    }

    /**
     * Обработчик исключения {@link HttpRequestMethodNotSupportedException}
     * Возникает когда обработчик запросов не поддерживает определенный метод запроса
     *
     * @param e Исключение {@link HttpRequestMethodNotSupportedException}
     * @return Объект {@link ErrorResponse} c информацией об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработчик всевозможных исключений во время работы программы
     *
     * @param e Исключение {@link Throwable}
     * @return Объект {@link ErrorResponse} c информацией об ошибке
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return new ErrorResponse(
                "Произошла непредвиденная ошибка."
        );
    }
}