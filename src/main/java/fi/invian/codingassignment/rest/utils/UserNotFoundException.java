package fi.invian.codingassignment.rest.utils;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super(userId);
    }
}
