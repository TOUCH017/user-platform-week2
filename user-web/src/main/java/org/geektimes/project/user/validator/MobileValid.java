package org.geektimes.project.user.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MobileAnnotationValidator.class)
public @interface MobileValid {

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String message() default "手机号不合法";

}
