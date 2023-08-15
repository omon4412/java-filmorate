package ru.yandex.practicum.filmorate.validation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.util.Constants;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Десериализатор {@link LocalDate}.
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser jsonParser,
                                 DeserializationContext deserializationContext) throws IOException {
        try {
            String date = jsonParser.getText();
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(Constants.dataTimeFormat));
        } catch (DateTimeException e) {
            throw new IncorrectParameterException(
                    "Ошибка десериализации даты LocalDate. Необходимый формат: " + Constants.dataTimeFormat, true);
        }
    }
}
