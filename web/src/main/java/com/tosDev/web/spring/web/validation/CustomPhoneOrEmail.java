package com.tosDev.web.spring.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomPhoneOrEmailValidation.class)
public @interface CustomPhoneOrEmail {
    String message() default "Please input your phone or email address";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default {};
}
