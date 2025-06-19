package fi.invian.codingassignment.rest.utils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class HttpResponseBuilder {

    public static Response buildErrorRespons(Response.Status status, String message) {

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(message))
                .build();
    }
}
