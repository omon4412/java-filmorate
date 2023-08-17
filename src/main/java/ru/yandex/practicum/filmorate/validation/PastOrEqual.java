package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для проверки даты позднее заданой {date} или равно.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumDateValidator.class)
public @interface PastOrEqual {
    /**
     * Заданная дата, с которой производится сравнение.
     *
     * @return Заданная дата
     */
    String date() default "1980-01-01";

    /**
     * Формат даты.
     *
     * @return Формат даты
     */
    String format() default "yyyy-MM-dd";

    /**
     * Сообщение об ошибке.
     *
     * @return Сообщение об ошибке, если дата не удовлетворяет условиям
     */
    String message() default "Дата не должна быть ранее {date}";

    /**
     * groups.
     *
     * @return Class[]
     */
    Class<?>[] groups() default {};

    /**
     * payload.
     *
     * @return Class[]
     */
    Class<? extends Payload>[] payload() default {};
}
