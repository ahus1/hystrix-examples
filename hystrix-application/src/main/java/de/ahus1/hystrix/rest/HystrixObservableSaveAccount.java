package de.ahus1.hystrix.rest;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import de.ahus1.hystrix.base.AbstractSaveAccount;
import de.ahus1.hystrix.base.Account;
import de.ahus1.hystrix.base.IBANValidator;
import de.ahus1.hystrix.base.ValidationException;

@Path("/observable")
@Api("/observable")
public class HystrixObservableSaveAccount extends AbstractSaveAccount {

    private static Logger LOG = LoggerFactory
            .getLogger(HystrixObservableSaveAccount.class);

    private static class IBANValidatorCommand extends
            HystrixObservableCommand<Boolean> {
        private List<Account> accounts;

        protected IBANValidatorCommand(List<Account> accounts) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory
                    .asKey("iban")));
            this.accounts = accounts;
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> observer) {
                    try {
                        if (!observer.isUnsubscribed()) {
                            for (Account account : accounts) {
                                observer.onNext(IBANValidator.isValid(account));
                            }
                            observer.onCompleted();
                        }
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                }

            });
        }
    }

    @POST
    @Path("/observable")
    @ApiOperation("save account data")
    public Response nonsenseObservable(List<Account> accounts)
            throws ValidationException, InterruptedException {

        Observable<Boolean> result =
                new IBANValidatorCommand(accounts).observe();

        result.forEach(new Action1<Boolean>() {
            @Override
            public void call(Boolean b) {
                LOG.info("say hi to b:" + b);
            }
        });

        result.forEach(b -> LOG.info("say hi to b:" + b));

        return Response.status(Status.OK).build();
    }
}
