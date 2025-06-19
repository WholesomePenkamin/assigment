package fi.invian.codingassignment.database;


import fi.invian.codingassignment.app.DatabaseConnection;
import fi.invian.codingassignment.rest.utils.UserNotFoundException;
import org.glassfish.jersey.internal.guava.CacheBuilder;
import org.glassfish.jersey.internal.guava.CacheLoader;
import org.glassfish.jersey.internal.guava.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Singleton
public class UserCache {
    private static final Logger LOG = LoggerFactory.getLogger(UserCache.class);
    private final LoadingCache<Integer, String> userCache;

    public UserCache() {
        userCache = CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build(new CacheLoader<>() {
                    @Override
                    public String load(Integer userId) {
                        return loadUserNameFromDb(userId);
                    }
                });
    }

    public String getUserName(int userId) {
        try {
            return userCache.get(userId);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof UserNotFoundException) {
                throw (UserNotFoundException) cause;
            }
            LOG.warn("Unexpected error during user lookup", e);
            throw new RuntimeException("Unexpected error while validating user ID " + userId, e);
        }
    }

    public void assertValidUser(int userId) {
        getUserName(userId);
    }
    //WILL THROW IF INVALID
    public void assertValidUsers(Set<Integer> userIds) {
        for (Integer id : userIds) {
            assertValidUser(id);
        }
    }

    private String loadUserNameFromDb(int userId) {
        String sql = "SELECT name FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            } else {
                throw new UserNotFoundException("User not found: " + userId);
            }

        } catch (SQLException e) {
            LOG.error("DB error while checking user {}", userId, e);
            throw new RuntimeException("Failed to validate user ID due to DB error", e);
        }
    }
}

