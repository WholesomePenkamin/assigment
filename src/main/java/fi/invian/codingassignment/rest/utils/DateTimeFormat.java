package fi.invian.codingassignment.rest.utils;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateTimeFormatValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimeFormat {
    String message() default "Invalid datetime format. Use yyyy-MM-dd'T'HH:mm:ss";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
