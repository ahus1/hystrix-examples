package de.ahus1.hystrix.base;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

// tag::classdef[]
@Provider
public class ValidationExceptionMapper implements
        ExceptionMapper<ValidationException> {

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(ValidationException e) {
        return Response.status(Status.BAD_REQUEST)
                .entity(new Message(e.getMessage()))
                .type(headers.getMediaType()).build();
    }

}
// end::classdef[]
