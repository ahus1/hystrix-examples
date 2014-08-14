package de.ahus1.hystrix.base;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InterruptedExceptionMapper implements
        ExceptionMapper<InterruptedException> {

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(InterruptedException e) {
        return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(new Message(e.getMessage()))
                .type(headers.getMediaType()).build();
    }

}
