package de.ahus1.hystrix.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.exception.HystrixRuntimeException;

import de.ahus1.hystrix.base.AbstractSaveAccount;
import de.ahus1.hystrix.base.Account;
import de.ahus1.hystrix.base.IBANValidator;
import de.ahus1.hystrix.base.ValidationException;

@Path("/hystrix")
public class HystrixSaveAccount extends AbstractSaveAccount {

    private static Logger LOG = LoggerFactory
            .getLogger(HystrixSaveAccount.class);

    @GET
    @Produces("text/plain")
    public Response hello() {
        return Response.status(Status.OK).entity("Hello world").build();
    }

    // tag::hystrix[]
    private static class IBANValidatorCommand extends HystrixCommand<Boolean> {
        private Account account;

        protected IBANValidatorCommand(Account account) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory
                    .asKey("iban")));
            this.account = account; // <2>
        }

        @Override
        protected Boolean run() throws Exception {
            return IBANValidator.isValid(account); // <3>
        }

    }

    @POST
    public Response save(Account account) throws ValidationException,
            InterruptedException {
        try {
            if (!new IBANValidatorCommand(account).execute()) { // <1>
                throw new ValidationException("invalid");
            }
        } catch (HystrixRuntimeException e) { // <4>
            if (e.getCause() instanceof InterruptedException) {
                throw (InterruptedException) e.getCause();
            }
            LOG.info("problem", e);
            return Response.status(Status.SERVICE_UNAVAILABLE).build();
        }
        super.saveToDatabase(account);
        return Response.status(Status.OK).build();
    }
    // end::hystrix[]
}
