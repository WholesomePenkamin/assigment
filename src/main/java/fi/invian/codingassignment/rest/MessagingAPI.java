package fi.invian.codingassignment.rest;

import fi.invian.codingassignment.database.MessageDAO;
import fi.invian.codingassignment.pojos.MessageParameters;
import fi.invian.codingassignment.pojos.MessageResponse;
import fi.invian.codingassignment.pojos.UserWithMessageCount;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/message")
@Produces(MediaType.APPLICATION_JSON)
public class MessagingAPI {

    @Inject
    MessageDAO messageDAO;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/add")
    public void addMessage(@Valid MessageParameters messageParameters) {
        messageDAO.saveMessage(messageParameters);
    }

    @GET
    @Path("/user/{user-id}")
    public List<MessageResponse> getMessagesForUser(@Positive @PathParam("user-id") int userId) {
        try {
            return messageDAO.getMessagesForUser(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/statistics/top-senders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserWithMessageCount> getTopSenders() throws SQLException {
        return messageDAO.getTopSendersLast30Days();
    }
}
