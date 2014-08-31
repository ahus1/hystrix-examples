package de.ahus1.hystrix.util.riemann;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aphyr.riemann.Proto.Event;
import com.aphyr.riemann.client.RiemannClient;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;

import de.ahus1.hystrix.base.IBANValidator;

/**
 * The Events are queued and sent from a second thread every 100 ms.
 */
public class HystrixRiemannEventNotifier extends HystrixEventNotifier {

    private static final int SEND_INTERVAL = 100;

    private static final int MAX_SIZE_EVENT_QUEUE = 10000;

    private static final int MAX_CHUNK_SIZE = 100;

    private static Logger LOG = LoggerFactory.getLogger(IBANValidator.class);

    private volatile RiemannClient c;

    private volatile boolean started;

    private final AtomicInteger bufferSize = new AtomicInteger();

    private final LinkedTransferQueue<Event> buffer =
            new java.util.concurrent.LinkedTransferQueue<Event>();

    private Timer timer;

    /**
     * Put an event in the event queue (if riemann has been started and there is
     * space in the queue). Otherwise the event is discarded silently.
     */
    @Override
    public void markEvent(HystrixEventType eventType, HystrixCommandKey key) {
        if (started) {
            if (bufferSize.get() < MAX_SIZE_EVENT_QUEUE) {
                buffer.add(c.event().service(key.name())
                        .state(eventType.name()).time(
                                System.currentTimeMillis()).build());
                bufferSize.incrementAndGet();
            }
        }
    }

    /**
     * Put an event in the event queue (if riemann has been started and there is
     * space in the queue). Otherwise the event is discarded silently.
     */
    @Override
    public void markCommandExecution(HystrixCommandKey key,
            ExecutionIsolationStrategy isolationStrategy, int duration,
            List<HystrixEventType> eventsDuringExecution) {
        if (started) {
            if (bufferSize.get() < MAX_SIZE_EVENT_QUEUE) {
                buffer.add(c.event().service(key.name()).metric(duration)
                        .build());
                bufferSize.incrementAndGet();
            }
        }
    }

    /**
     * Start the scheduler to transmit events to Riemann.
     */
    public synchronized void start() {
        TimerTask timerTask = new SenderTask();
        // running timer task as daemon thread
        timer = new Timer(true);
        timer.schedule(timerTask, 0, SEND_INTERVAL);
        started = true;
    }

    /**
     * Stop the scheduler to transmit events to Riemann and also the Riemann
     * client.
     */
    public synchronized void stop() throws IOException {
        try {
            if (started) {
                started = false;
                timer.cancel();
                timer = null;
                if (c != null) {
                    c.disconnect();
                }
                c = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loc-kfree check if connection to Riemann server has already been created.
     * If it doesn't exist yet, will try to establish it.
     * 
     * @return RiemannClient that is readily connected.
     * @throws IOException
     *             on problems
     */
    private RiemannClient getClient() throws IOException {
        RiemannClient localClient = c;
        if (localClient == null || !localClient.isConnected()) {
            localClient = connect();
        }
        return localClient;
    }

    /**
     * Synchronized method to connect to Riemann server.
     * 
     * @return RiemannClient that is readily connected.
     * @throws IOException
     *             on problems
     */
    private synchronized RiemannClient connect() throws IOException {
        if (c == null) {
            c = RiemannClient.tcp("localhost", 5555);
        }
        if (!c.isConnected()) {
            c.connect();
        }
        return c;
    }

    /**
     * Send over events to Riemann in batches.
     */
    class SenderTask extends TimerTask {

        @Override
        public void run() {
            if (started) {
                try {
                    RiemannClient c = getClient();
                    do {
                        final int chunkSize =
                                Math.min(MAX_CHUNK_SIZE, bufferSize.get());
                        if (chunkSize > 0) {
                            // Allocate space for writes
                            final ArrayList<Event> events =
                                    new ArrayList<Event>(chunkSize);
                            // Suck down elements from queue
                            buffer.drainTo(events, chunkSize);
                            // Update count
                            bufferSize.addAndGet(-1 * events.size());
                            c.sendEvents(events);
                        }
                    } while (bufferSize.get() > MAX_CHUNK_SIZE);
                } catch (IOException e) {
                    LOG.error("unable to connect to riemann");
                }
            }
        }

    }

}
