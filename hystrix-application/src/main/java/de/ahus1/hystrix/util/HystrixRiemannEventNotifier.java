package de.ahus1.hystrix.util;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aphyr.riemann.client.RiemannClient;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;

import de.ahus1.hystrix.base.IBANValidator;

public class HystrixRiemannEventNotifier extends HystrixEventNotifier {

    private static Logger LOG = LoggerFactory.getLogger(IBANValidator.class);

    private RiemannClient c;

    public HystrixRiemannEventNotifier() throws IOException {
        c = RiemannClient.tcp("localhost", 5555);
        c.connect();
    }

    @Override
    public void markEvent(HystrixEventType eventType, HystrixCommandKey key) {
        try {
            c.event().service(key.name()).state(eventType.name()).send()
                    .deref(10, TimeUnit.SECONDS);
        } catch (IOException e) {
            LOG.error("unable to send event", e);
        }
    }

    @Override
    public void markCommandExecution(HystrixCommandKey key,
            ExecutionIsolationStrategy isolationStrategy, int duration,
            List<HystrixEventType> eventsDuringExecution) {
        try {
            c.event().service(key.name()).metric(duration).send()
                    .deref(10, TimeUnit.SECONDS);
        } catch (IOException e) {
            LOG.error("unable to send event", e);
        }
        super.markCommandExecution(key, isolationStrategy, duration,
                eventsDuringExecution);
    }

    public void stop() throws IOException {
        c.disconnect();
    }

}
