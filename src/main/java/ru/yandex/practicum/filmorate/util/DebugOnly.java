package ru.yandex.practicum.filmorate.util;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DebugOnly {
    String value() default "";
}
