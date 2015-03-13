package de.ahus1.hystrix.rest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import de.ahus1.hystrix.base.AbstractSaveAccount;
import de.ahus1.hystrix.base.Account;
import de.ahus1.hystrix.base.IBANValidator;
import de.ahus1.hystrix.base.ValidationException;

@Path("/future")
@Api("/future")
public class HystrixFutureSaveAccount extends AbstractSaveAccount {

    private static Logger LOG = LoggerFactory
            .getLogger(HystrixFutureSaveAccount.class);

    private static class IBANValidatorCommand extends HystrixCommand<Boolean> {
        private Account account;

        protected IBANValidatorCommand(Account account) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory
                    .asKey("iban")));
            this.account = account;
        }

        @Override
        protected Boolean run() throws Exception {
            return IBANValidator.isValid(account);
        }

    }

    @POST
    @Path("/future")
    @ApiOperation("save account data")
    public Response saveFuture(Account account) throws ValidationException,
            InterruptedException, ExecutionException {

        Future<Boolean> result = new IBANValidatorCommand(account).queue();
        if (result.isDone()) {
            // yeah!
        }
        if (!result.get()) {
            throw new ValidationException("invalid");
        }

        super.saveToDatabase(account);
        return Response.status(Status.OK).build();
    }

}
