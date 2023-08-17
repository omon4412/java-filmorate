package ru.yandex.practicum.filmorate.validation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.util.Constants;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Сериализатор {@link LocalDate}.
 */
public class LocalDateSerializer extends JsonSerializer<LocalDate> {
    @Override
    public void serialize(LocalDate date, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        try {
            String formattedDate = date.format(DateTimeFormatter.ofPattern(Constants.dataTimeFormat));
            jsonGenerator.writeString(formattedDate);
        } catch (DateTimeException e) {
            throw new IncorrectParameterException(
                    "Ошибка сериализации даты LocalDate. Необходимый формат: " + Constants.dataTimeFormat, true);
        }
    }
}
