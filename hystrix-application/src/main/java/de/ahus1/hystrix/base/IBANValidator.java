package de.ahus1.hystrix.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;

// tag::classdef[]
public class IBANValidator {

    private static Logger LOG = LoggerFactory.getLogger(IBANValidator.class);

    // tag::dynprop[]
    private static DynamicLongProperty timeToWait = DynamicPropertyFactory
            .getInstance().getLongProperty("hystrixdemo.sleep", 100);

    // end::dynprop[]

    public static synchronized boolean isValid(Account account)
            throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        long t = timeToWait.get();
        LOG.info("waiting {} ms", t);
        if (t > 0) {
            Thread.sleep(t);
        }
        return true;
    }
}
// end::classdef[]