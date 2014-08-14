package de.ahus1.hystrix.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.util.HystrixRollingNumberEvent;
import com.quigley.zabbixj.metrics.MetricsException;
import com.quigley.zabbixj.metrics.MetricsKey;
import com.quigley.zabbixj.metrics.MetricsProvider;

public class ZabbixCommandMetricsProvider implements MetricsProvider {

    private static Logger LOG = LoggerFactory
            .getLogger(ZabbixCommandMetricsProvider.class);

    Map<String, HystrixRollingNumberEvent> numberEvents = new HashMap<>();
    {
        numberEvents.put("CollapsedRequests",
                HystrixRollingNumberEvent.COLLAPSED);
        numberEvents.put("ExceptionsThrown",
                HystrixRollingNumberEvent.EXCEPTION_THROWN);
        numberEvents.put("Failure", HystrixRollingNumberEvent.FAILURE);
        numberEvents.put("FallbackFailure",
                HystrixRollingNumberEvent.FALLBACK_FAILURE);
        numberEvents.put("FallbackRejection",
                HystrixRollingNumberEvent.FALLBACK_REJECTION);
        numberEvents.put("FallbackSuccess",
                HystrixRollingNumberEvent.FALLBACK_SUCCESS);
        numberEvents.put("ResponsesFromCache",
                HystrixRollingNumberEvent.RESPONSE_FROM_CACHE);
        numberEvents.put("SemaphoreRejected",
                HystrixRollingNumberEvent.SEMAPHORE_REJECTED);
        numberEvents.put("ShortCircuited",
                HystrixRollingNumberEvent.SHORT_CIRCUITED);
        numberEvents.put("Success", HystrixRollingNumberEvent.SUCCESS);
        numberEvents.put("ThreadPoolRejected",
                HystrixRollingNumberEvent.THREAD_POOL_REJECTED);
        numberEvents.put("Timeout", HystrixRollingNumberEvent.TIMEOUT);
    }

    Map<String, HystrixZabbixMetricsPublisherCommand> map = new HashMap<>();

    @Override
    public Object getValue(MetricsKey key) throws MetricsException {
        if (key.getKey().equals("discovery")) {
            try {
                JSONObject discovery = new JSONObject();
                JSONArray array = new JSONArray();
                discovery.put("data", array);
                for (String command : map.keySet()) {
                    JSONObject element = new JSONObject();
                    element.put("{#COMMAND}", command);
                    array.put(element);
                }
                LOG.info("discovery: {}", discovery);
                return discovery;
            } catch (JSONException e) {
                throw new MetricsException(e);
            }
        } else if (key.getKey().startsWith("count")) {
            HystrixRollingNumberEvent event = numberEvents.get(key.getKey()
                    .replaceFirst("count", ""));
            if (event == null) {
                throw new MetricsException("unknown key " + key);
            }
            HystrixZabbixMetricsPublisherCommand command = map.get(key
                    .getParameters()[0]);
            if (command == null) {
                throw new MetricsException("unknown command referenced in "
                        + key);
            }
            return command.getMetrics().getCumulativeCount(event);
        } else if (key.getKey().equals("latencyExecute")) {
            HystrixZabbixMetricsPublisherCommand command = map.get(key
                    .getParameters()[0]);
            if (command == null) {
                throw new MetricsException("unknown command referenced in "
                        + key);
            }
            String percentile = key.getParameters()[1];
            if (percentile.equalsIgnoreCase("mean")) {
                return command.getMetrics().getTotalTimeMean();
            } else {
                return command.getMetrics().getTotalTimePercentile(
                        Double.parseDouble(percentile));
            }
        } else if (key.getKey().equals("latencyTotal")) {
            HystrixZabbixMetricsPublisherCommand command = map.get(key
                    .getParameters()[0]);
            if (command == null) {
                throw new MetricsException("unknown command referenced in "
                        + key);
            }
            String percentile = key.getParameters()[1];
            if (percentile.equalsIgnoreCase("mean")) {
                return command.getMetrics().getTotalTimeMean();
            } else {
                return command.getMetrics().getTotalTimePercentile(
                        Double.parseDouble(percentile));
            }
        } else if (key.getKey().equals("latencyExecute")) {
            HystrixZabbixMetricsPublisherCommand command = map.get(key
                    .getParameters()[0]);
            if (command == null) {
                throw new MetricsException("unknown command referenced in "
                        + key);
            }
            String percentile = key.getParameters()[1];
            if (percentile.equalsIgnoreCase("mean")) {
                return command.getMetrics().getExecutionTimeMean();
            } else {
                return command.getMetrics().getExecutionTimePercentile(
                        Double.parseDouble(percentile));
            }
        } else {
            throw new MetricsException("unknown key " + key);
        }
    }

    public void register(HystrixZabbixMetricsPublisherCommand command) {
        map.put(command.getKey().name(), command);
    }
}
