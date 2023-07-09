package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для проверки даты позднее задайно {date} или равно
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumDateValidator.class)
public @interface PastOrEqual {
    String date() default "1980-01-01";

    String format() default "yyyy-MM-dd";

    String message() default "Дата не должна быть ранее {date}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
