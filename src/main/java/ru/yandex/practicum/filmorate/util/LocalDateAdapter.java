package ru.yandex.practicum.filmorate.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Адаптер для роботы с {@link LocalDate}.
 * Нужно для использования свого сериализатора и дессериализатора
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    /**
     * Шаблон для даты.
     */
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.dataTimeFormat);

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
        jsonWriter.value(localDate.format(formatter));
    }

    @Override
    public LocalDate read(final JsonReader jsonReader) throws IOException {
        return LocalDate.parse(jsonReader.nextString(), formatter);
    }
}
