package fi.invian.codingassignment.rest.utils;

public class InternalErrorException extends RuntimeException {
    public InternalErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}

