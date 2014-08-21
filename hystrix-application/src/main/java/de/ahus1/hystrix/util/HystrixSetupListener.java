package de.ahus1.hystrix.util;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.jmx.BaseConfigMBean;
import com.netflix.config.jmx.ConfigMBean;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;

/**
 * Setup and teardown of Hystrix configuration at start/end of application
 * lifecycle.
 * 
 * @author Alexander Schwartz (msg systems ag) 2014
 * @version 3.3
 */
@WebListener
public class HystrixSetupListener implements ServletContextListener {

    private static Logger LOG = LoggerFactory
            .getLogger(ZabbixCommandMetricsProvider.class);

    /** for register and un-register. */
    static final String OBJ_NAME_LOG4J_BEAN =
            "de.ahus1.archaius:type=ArchaiusMBean";

    /** Object name for JMX binding. */
    private ObjectName name;

    private final static DynamicBooleanProperty enablezabbix =
            DynamicPropertyFactory.getInstance().getBooleanProperty(
                    "hystrixdemo.enablezabbix", false);

    private HystrixZabbixMetricsPublisher zabbix;

    private final static DynamicBooleanProperty enableriemann =
            DynamicPropertyFactory.getInstance().getBooleanProperty(
                    "hystrixdemo.enableriemann", false);

    private HystrixRiemannEventNotifier riemann;

    /**
     * Setup Servo and other Hystrix elements, and JMX Registration of Archaius.
     * 
     * @param sce
     *            servlet context event
     */
    public void contextInitialized(ServletContextEvent sce) {
        AbstractConfiguration conf = ConfigurationManager.getConfigInstance();

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // register Archaius as MBean to allow runtime configuration.
        try {
            name =
                    new ObjectName(OBJ_NAME_LOG4J_BEAN
                            + sce.getServletContext().getContextPath());
            BaseConfigMBean bean = new BaseConfigMBean(conf);
            StandardMBean mbean = new StandardMBean(bean, ConfigMBean.class);
            mbs.registerMBean(mbean, name);
        } catch (InstanceAlreadyExistsException | MalformedObjectNameException
                | NotCompliantMBeanException | MBeanRegistrationException e) {
            throw new RuntimeException(e);
        }

        // expose all Hystrix methods as JMX Beans
        // HystrixPlugins.getInstance().registerMetricsPublisher(
        // HystrixServoMetricsPublisher.getInstance());

        setupZabbix();

        setupRiemann();

    }

    private void setupRiemann() {
        riemann = new HystrixRiemannEventNotifier();
        HystrixPlugins.getInstance().registerEventNotifier(riemann);

        enableriemann.addCallback(new Runnable() {
            @Override
            public void run() {
                try {
                    if (enableriemann.getValue()) {
                        LOG.info("starting riemann");
                        riemann.start();
                    } else {
                        LOG.info("stopping riemann");
                        riemann.stop();
                    }
                } catch (Exception e) {
                    LOG.warn("exception", e);
                }
            }
        });

        if (enableriemann.getValue() == true) {
            try {
                riemann.start();
            } catch (IOException e) {
                /*
                 * TODO: find a way for lazy registration as soon as Riemann is
                 * up and running
                 */
                LOG.error("unable to register Riemann listener", e);
            }
        }
    }

    private void setupZabbix() {
        try {
            zabbix = new HystrixZabbixMetricsPublisher();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        enablezabbix.addCallback(new Runnable() {
            @Override
            public void run() {
                try {
                    if (enablezabbix.getValue()) {
                        LOG.info("starting zabbix");
                        zabbix.start();
                    } else {
                        LOG.info("stopping zabbix");
                        zabbix.stop();
                    }
                } catch (Exception e) {
                    LOG.warn("exception", e);
                }
            }
        });

        if (enablezabbix.getValue()) {
            try {
                zabbix.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Cleanup JMX Registration of Archaius.
     * 
     * @param sce
     *            servlet context event
     */
    public void contextDestroyed(ServletContextEvent sce) {

        if (riemann != null) {
            try {
                riemann.stop();
            } catch (IOException e) {
                LOG.error("unable to shut down riemann connector", e);
            }
        }

        if (zabbix != null) {
            zabbix.stop();
        }

        // de-register Archaius MBean
        if (name != null) {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            try {
                mbs.unregisterMBean(name);
            } catch (MBeanRegistrationException | InstanceNotFoundException e) {
                LOG.info("problem de-registering " + e);
            }
        }

        // tag::hystrixstop[]
        // shutdown all thread pools; waiting a little time for shutdown
        Hystrix.reset(1, TimeUnit.SECONDS);
        // end::hystrixstop[]

        // tag::archaiusstop[]
        // shutdown configuration listeners that might have been activated by
        // Archaius
        if (ConfigurationManager.getConfigInstance() instanceof DynamicConfiguration) {
            ((DynamicConfiguration) ConfigurationManager.getConfigInstance())
                    .stopLoading();
        } else if (ConfigurationManager.getConfigInstance() instanceof ConcurrentCompositeConfiguration) {
            ConcurrentCompositeConfiguration config =
                    ((ConcurrentCompositeConfiguration) ConfigurationManager
                            .getConfigInstance());
            for (AbstractConfiguration innerConfig : config.getConfigurations()) {
                if (innerConfig instanceof DynamicConfiguration) {
                    ((DynamicConfiguration) innerConfig).stopLoading();
                }
            }
        }
        // end::archaiusstop[]

    }
}
