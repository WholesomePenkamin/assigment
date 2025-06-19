package fi.invian.codingassignment.rest;

import fi.invian.codingassignment.database.MessageDAO;
import fi.invian.codingassignment.database.UserCache;
import fi.invian.codingassignment.pojos.MessageParameters;
import fi.invian.codingassignment.rest.utils.HttpResponseBuilder;
import fi.invian.codingassignment.rest.utils.UserNotFoundException;
import org.glassfish.jersey.internal.guava.UncheckedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/message")
@Produces(MediaType.APPLICATION_JSON)
public class MessagingAPI {

    private static final Logger LOG = LoggerFactory.getLogger(MessagingAPI.class);

    @Inject
    MessageDAO messageDAO;

    @Inject
    UserCache userCache;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/add")
    public Response addMessage(@Valid MessageParameters messageParameters) {
        try {
            userCache.assertValidUser(messageParameters.getSenderId());
            userCache.assertValidUsers(messageParameters.getRecipientIds());

        } catch (UncheckedExecutionException e) {
            return e.getCause() instanceof UserNotFoundException ?
                    HttpResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage()) :
                    HttpResponseBuilder.buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Server error occurred.");
        }
        messageDAO.saveMessage(messageParameters);
        return Response.status(Response.Status.CREATED).build();
    }


    @GET
    @Path("/user/{user-id}")
    public Response getMessagesForUser(@Positive @PathParam("user-id") int userId) {
        try {
            userCache.assertValidUser(userId);
        } catch (UncheckedExecutionException e) {
            return e.getCause() instanceof UserNotFoundException ?
                    HttpResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage()) :
                    HttpResponseBuilder.buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Server error occurred.");
        } //TODO fetch from cache instantly. Response duplication
        return Response.ok(messageDAO.getMessagesForUser(userId)).build();
    }

    @GET
    @Path("/statistics/top-senders")
    public Response getTopSenders() {
        return Response.ok(messageDAO.getTopSendersLast30Days()).build();
    }
}
