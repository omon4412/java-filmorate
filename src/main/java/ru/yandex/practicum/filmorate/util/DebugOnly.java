package ru.yandex.practicum.filmorate.util;

import java.lang.annotation.*;

/**
 * Аннотация для пометки методов.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DebugOnly {
    /**
     * Дополнительное сообщение или описание.
     *
     * @return Сообщение или описание для метода
     */
    String value() default "";
}
