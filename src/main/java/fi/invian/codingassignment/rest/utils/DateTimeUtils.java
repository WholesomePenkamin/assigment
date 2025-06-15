package fi.invian.codingassignment.rest.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class DateTimeUtils {

    public static LocalDateTime stringToDateTime(String sentAt) {
        try {
            return LocalDateTime.parse(sentAt);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date-time format: " + sentAt, e);
        }
    }
}
