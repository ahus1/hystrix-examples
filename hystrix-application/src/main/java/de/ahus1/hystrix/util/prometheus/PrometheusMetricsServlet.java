package de.ahus1.hystrix.util.prometheus;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;

import javax.servlet.annotation.WebServlet;

/**
 * @author Alexander Schwartz 2016
 */
@WebServlet(urlPatterns = "/metrics")
public class PrometheusMetricsServlet extends MetricsServlet {

    public PrometheusMetricsServlet() {
        super();
        DefaultExports.initialize();
    }
}
