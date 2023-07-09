package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Варидатор для аннатации {@link PastOrEqual}
 */
public class MinimumDateValidator implements ConstraintValidator<PastOrEqual, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(PastOrEqual constraintAnnotation) {
        minDate = LocalDate.parse(constraintAnnotation.date(),
                DateTimeFormatter.ofPattern(constraintAnnotation.format()));
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        return releaseDate != null && !releaseDate.isBefore(minDate);
    }
}
