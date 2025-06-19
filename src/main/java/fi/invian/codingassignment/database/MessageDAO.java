package fi.invian.codingassignment.database;

import fi.invian.codingassignment.app.DatabaseConnection;
import fi.invian.codingassignment.pojos.MessageParameters;
import fi.invian.codingassignment.pojos.MessageResponse;
import fi.invian.codingassignment.pojos.User;
import fi.invian.codingassignment.pojos.UserWithMessageCount;
import fi.invian.codingassignment.rest.utils.DateTimeUtils;
import fi.invian.codingassignment.rest.utils.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Singleton
public class MessageDAO {

    private static final String SQL_GET_USER_MESSAGES =  "SELECT " +
            "    m.id AS message_id, " +
            "    m.sender_id, " +
            "    s.name AS sender_name, " +
            "    m.title, " +
            "    m.body, " +
            "    m.sent_at " +
            "FROM messages m " +
            "JOIN users s ON m.sender_id = s.id " +
            "JOIN message_recipients r ON m.id = r.message_id " +
            "WHERE r.recipient_id = ?";

    private static final String SQL_GET_USER_STATISTICS = "SELECT u.id, u.name, COUNT(m.id) AS message_count " +
            "FROM users u " +
            "JOIN messages m ON m.sender_id = u.id " +
            "WHERE m.sent_at >= NOW() - INTERVAL 30 DAY " +
            "GROUP BY u.id, u.name " +
            "ORDER BY message_count DESC " +
            "LIMIT 10";

    private static final String SQL_INSERT_MESSAGE = "INSERT INTO messages (sender_id, title, body, sent_at) VALUES (?, ?, ?, ?)";
    private static final String SQL_INSERT_MESSAGE_RECIPIENT = "INSERT INTO message_recipients (message_id, recipient_id) VALUES (?, ?)";

    private static final Logger LOG = LoggerFactory.getLogger(MessageDAO.class);

    public void saveMessage(MessageParameters messageParameters) {
        Set<Integer> recipientIds = messageParameters.getRecipientIds();

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int messageId = saveMessageAndGetId(conn, messageParameters);
            saveRecipients(conn, messageId, recipientIds);

            conn.commit();

        } catch (SQLException e) {
            LOG.error("Failed to save message and recipients. Iniating rollback", e);
            DatabaseConnection.rollback(conn);
            throw new InternalErrorException("Database error. Saving of messages failed", e);

        } finally {
            LOG.debug("closing transaction");
            DatabaseConnection.close(conn);
        }
    }

    private int saveMessageAndGetId(Connection conn, MessageParameters messageParameters) throws SQLException {

        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_MESSAGE, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, messageParameters.getSenderId());
            stmt.setString(2, messageParameters.getTitle());
            stmt.setString(3, messageParameters.getBody());
            stmt.setTimestamp(4, Timestamp.valueOf(DateTimeUtils.stringToDateTime(messageParameters.getSentAt())));
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating message failed, no ID obtained.");
                }
            }
        }
    }

    private void saveRecipients(Connection conn, int messageId, Set<Integer> recipientIds) throws SQLException {

        try (PreparedStatement recipientStmt = conn.prepareStatement(SQL_INSERT_MESSAGE_RECIPIENT)) {
            for (Integer recipientId : recipientIds) {
                recipientStmt.setInt(1, messageId);
                recipientStmt.setInt(2, recipientId);
                recipientStmt.addBatch();
            }
            recipientStmt.executeBatch();
        }
    }

    public List<MessageResponse> getMessagesForUser(int userId)  {
        List<MessageResponse> messages = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement messageStmt = conn.prepareStatement(SQL_GET_USER_MESSAGES)) {

            messageStmt.setInt(1, userId);
            ResultSet rs = messageStmt.executeQuery();

            while (rs.next()) {
                int messageId = rs.getInt("message_id");
                User sender = new User(
                        rs.getInt("sender_id"),
                        rs.getString("sender_name")
                );
                MessageResponse msg = new MessageResponse(
                        messageId,
                        sender,
                        rs.getString("title"),
                        rs.getString("body"),
                        rs.getTimestamp("sent_at").toLocalDateTime().toString()
                );

                messages.add(msg);
            }
        } catch (SQLException e) {
            LOG.error("Failed to get messages for user", e);
            throw new InternalErrorException("Database error. Failed to get messages for user", e);
        }
        return messages;
    }

    public List<UserWithMessageCount> getTopSendersLast30Days() {
        List<UserWithMessageCount> topSenders = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_USER_STATISTICS);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("name"));
                int count = rs.getInt("message_count");
                topSenders.add(new UserWithMessageCount(user, count));
            }
        } catch (SQLException e) {
            LOG.error("Failed to get top senders", e);
            throw new InternalErrorException("Database error. Failed to get user statistics", e);
        }
        return topSenders;
    }
}
