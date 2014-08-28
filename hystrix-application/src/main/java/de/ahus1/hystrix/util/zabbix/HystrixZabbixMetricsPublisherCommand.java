package de.ahus1.hystrix.util.zabbix;

import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherCommand;

public class HystrixZabbixMetricsPublisherCommand implements
        HystrixMetricsPublisherCommand {

    private final HystrixCommandKey key;
    private final HystrixCommandGroupKey commandGroupKey;
    private final HystrixCommandMetrics metrics;
    private final HystrixCircuitBreaker circuitBreaker;
    private final HystrixCommandProperties properties;
    private final ZabbixCommandMetricsProvider commandProvider;

    public HystrixZabbixMetricsPublisherCommand(HystrixCommandKey commandKey,
            HystrixCommandGroupKey commandGroupKey,
            HystrixCommandMetrics metrics,
            HystrixCircuitBreaker circuitBreaker,
            HystrixCommandProperties properties,
            ZabbixCommandMetricsProvider commandProvider) {
        this.key = commandKey;
        this.commandGroupKey = commandGroupKey;
        this.metrics = metrics;
        this.circuitBreaker = circuitBreaker;
        this.properties = properties;
        this.commandProvider = commandProvider;
    }

    @Override
    public void initialize() {
        commandProvider.register(this);
    }

    public HystrixCommandKey getKey() {
        return key;
    }

    public HystrixCommandGroupKey getCommandGroupKey() {
        return commandGroupKey;
    }

    public HystrixCommandMetrics getMetrics() {
        return metrics;
    }

    public HystrixCircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }

    public HystrixCommandProperties getProperties() {
        return properties;
    }

}
