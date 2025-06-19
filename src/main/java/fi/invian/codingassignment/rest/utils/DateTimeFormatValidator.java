package fi.invian.codingassignment.rest.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeFormatValidator implements ConstraintValidator<DateTimeFormat, String> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return false;
        try {
            FORMATTER.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
