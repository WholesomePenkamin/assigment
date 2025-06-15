package fi.invian.codingassignment.database;

import fi.invian.codingassignment.app.DatabaseConnection;
import fi.invian.codingassignment.pojos.MessageParameters;
import fi.invian.codingassignment.pojos.MessageResponse;
import fi.invian.codingassignment.pojos.User;
import fi.invian.codingassignment.pojos.UserWithMessageCount;
import fi.invian.codingassignment.rest.utils.DateTimeUtils;

import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MessageDAO {

    public void saveMessage(MessageParameters messageParameters) {
        String insertMessageSQL = "INSERT INTO messages (sender_id, title, body, sent_at) VALUES (?, ?, ?, ?)";
        String insertRecipientSQL = "INSERT INTO message_recipients (message_id, recipient_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

           //messages
            try (PreparedStatement stmt = conn.prepareStatement(insertMessageSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, messageParameters.getSenderId());
                stmt.setString(2, messageParameters.getTitle());
                stmt.setString(3, messageParameters.getBody());
                stmt.setTimestamp(4, Timestamp.valueOf(DateTimeUtils.stringToDateTime(messageParameters.getSentAt())));
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (!generatedKeys.next()) {
                    throw new SQLException("Creating message failed, no ID obtained.");
                }
                int messageId = generatedKeys.getInt(1);

                // message_recipients
                try (PreparedStatement recipientStmt = conn.prepareStatement(insertRecipientSQL)) {
                    List<Integer> recipientIds = messageParameters.getRecipientIds();
                    if (recipientIds.size() > 5) { //TODO put this elsewhere
                        throw new IllegalArgumentException("A message can have a maximum of 5 recipients.");
                    }

                    for (Integer recipientId : recipientIds) {
                        recipientStmt.setInt(1, messageId);
                        recipientStmt.setInt(2, recipientId);
                        recipientStmt.addBatch();
                    }
                    recipientStmt.executeBatch();
                }
            }

            conn.commit(); // transaction end
        } catch (SQLException e) {
            throw new RuntimeException("Error saving message and recipients", e);
        }
    }

    public List<MessageResponse> getMessagesForUser(int userId) throws SQLException {
        List<MessageResponse> messages = new ArrayList<>();

        String messageSql =
                "SELECT " +
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

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement messageStmt = conn.prepareStatement(messageSql)) {

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
                        rs.getTimestamp("sent_at").toString()
                );

                messages.add(msg);
            }
        }

        return messages;
    }

    public List<UserWithMessageCount> getTopSendersLast30Days() throws SQLException {
        List<UserWithMessageCount> topSenders = new ArrayList<>();

        String sql =
                "SELECT u.id, u.name, COUNT(m.id) AS message_count " +
                        "FROM users u " +
                        "JOIN messages m ON m.sender_id = u.id " +
                        "WHERE m.sent_at >= NOW() - INTERVAL 30 DAY " +
                        "GROUP BY u.id, u.name " +
                        "ORDER BY message_count DESC " +
                        "LIMIT 10";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("name"));
                int count = rs.getInt("message_count");
                topSenders.add(new UserWithMessageCount(user, count));
            }
        }

        return topSenders;
    }

}
