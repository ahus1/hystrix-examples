/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ahus1.hystrix.util;

import java.net.InetAddress;

import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolMetrics;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherCommand;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherThreadPool;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherThreadPoolDefault;
import com.quigley.zabbixj.agent.ZabbixAgent;

/**
 * Zabbix implementation of {@link HystrixMetricsPublisher}.
 */
public class HystrixZabbixMetricsPublisher extends HystrixMetricsPublisher {
    private ZabbixAgent agent;
    private ZabbixCommandMetricsProvider commandProvider;
    boolean started;

    public HystrixZabbixMetricsPublisher() throws Exception {
        agent = new ZabbixAgent();
        agent.setEnableActive(true);
        agent.setEnablePassive(false);
        agent.setHostName("hystrix");
        agent.setServerAddress(InetAddress.getByName("127.0.0.1"));
        agent.setServerPort(10051);

        commandProvider = new ZabbixCommandMetricsProvider();
        agent.addProvider("hystrixCommand", commandProvider);

    }

    public synchronized void start() throws Exception {
        if (!started) {
            agent.start();
            started = true;
        }
    }

    public synchronized void stop() {
        if (started) {
            agent.stop();
            started = false;
        }
    }

    @Override
    public HystrixMetricsPublisherCommand getMetricsPublisherForCommand(
            HystrixCommandKey commandKey,
            HystrixCommandGroupKey commandGroupKey,
            HystrixCommandMetrics metrics,
            HystrixCircuitBreaker circuitBreaker,
            HystrixCommandProperties properties) {
        return new HystrixZabbixMetricsPublisherCommand(commandKey,
                commandGroupKey, metrics, circuitBreaker, properties,
                commandProvider);
    }

    @Override
    public HystrixMetricsPublisherThreadPool getMetricsPublisherForThreadPool(
            HystrixThreadPoolKey threadPoolKey,
            HystrixThreadPoolMetrics metrics,
            HystrixThreadPoolProperties properties) {
        return new HystrixMetricsPublisherThreadPoolDefault(threadPoolKey,
                metrics, properties);
    }
}
