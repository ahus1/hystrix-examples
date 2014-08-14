package de.ahus1.hystrix.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.ahus1.hystrix.base.AbstractSaveAccount;
import de.ahus1.hystrix.base.Account;
import de.ahus1.hystrix.base.IBANValidator;
import de.ahus1.hystrix.base.ValidationException;

// tag::classdef[]
@Path("/simple")
public class SimpleSaveAccount extends AbstractSaveAccount {

    @GET
    @Produces("text/plain")
    public Response hello() {
        return Response.status(Status.OK).entity("Hello world").build();
    }

    @POST
    public Response save(Account account) throws ValidationException,
            InterruptedException {
        if (!IBANValidator.isValid(account)) {
            throw new ValidationException("invalid");
        }
        super.saveToDatabase(account);
        return Response.status(Status.OK).build();
    }
}
// tag::classdef[]
