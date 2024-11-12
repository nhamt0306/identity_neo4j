package com.nhamt.book_store.validator;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;

@Target({FIELD}) //scope of annotation
@Retention(RetentionPolicy.RUNTIME) // When annotation work/approve
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstraint {
    //default method
    String message() default "Invalid date of birth";

    Class<?>[] groups() default {};

    Class<? extends jakarta.validation.Payload>[] payload() default {};

    //custom method
    int min();
}
