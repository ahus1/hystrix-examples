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

/**
 * TODO: the events should be queued and sent from another thread (ideally in a
 * batched mode every 100 ms).
 */
public class HystrixRiemannEventNotifier extends HystrixEventNotifier {

    private static Logger LOG = LoggerFactory.getLogger(IBANValidator.class);

    private RiemannClient c;

    private volatile boolean started;

    @Override
    public void markEvent(HystrixEventType eventType, HystrixCommandKey key) {
        try {
            if (started) {
                c.event().service(key.name()).state(eventType.name()).send()
                        .deref(10, TimeUnit.SECONDS);
            }
        } catch (IOException e) {
            LOG.error("unable to send event", e);
        }
    }

    @Override
    public void markCommandExecution(HystrixCommandKey key,
            ExecutionIsolationStrategy isolationStrategy, int duration,
            List<HystrixEventType> eventsDuringExecution) {
        try {
            if (started) {
                c.event().service(key.name()).metric(duration).send().deref(10,
                        TimeUnit.SECONDS);
            }
        } catch (IOException e) {
            LOG.error("unable to send event", e);
        }
        super.markCommandExecution(key, isolationStrategy, duration,
                eventsDuringExecution);
    }

    public synchronized void start() throws IOException {
        if (!started) {
            c = RiemannClient.tcp("localhost", 5555);
            c.connect();
            started = true;
        }
    }

    public synchronized void stop() throws IOException {
        if (started) {
            c.disconnect();
            c = null;
            started = false;
        }
    }

}
