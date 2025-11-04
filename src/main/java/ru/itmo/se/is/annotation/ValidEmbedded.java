package ru.itmo.se.is.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.itmo.se.is.validation.EmbeddedObjectValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmbeddedObjectValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmbedded {
    String message() default "Either id or value must be set, but not both";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean nullable() default false;
}
