package com.github.exabrial.logback.jmx;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.github.exabrial.logback.JmsAppender;

public final class JmsAppenderJmxCollector {
	private static final Map<JmsAppender<?>, ObjectName> appenderToIdMap = new ConcurrentHashMap<>();

	public static final void add(final JmsAppender<?> jmsAppender) {
		try {
			final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
			final ObjectName objectName = toObjectName(jmsAppender);
			final JmsAppenderStatsMBean mbean = new JmsAppenderStats(jmsAppender);
			platformMBeanServer.registerMBean(mbean, objectName);
			appenderToIdMap.put(jmsAppender, objectName);
		} catch (final MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException e) {
			System.err.println("WARNING: err while registering JmsAppenderStatsMBean for jmsAppender:" + jmsAppender);
			e.printStackTrace();
		}
	}

	public static final void remove(final JmsAppender<?> jmsAppender) {
		final ObjectName objectName = appenderToIdMap.get(jmsAppender);
		if (objectName != null) {
			appenderToIdMap.remove(jmsAppender);
			try {
				final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
				platformMBeanServer.unregisterMBean(objectName);
			} catch (MBeanRegistrationException | InstanceNotFoundException e) {
				System.err.println("WARNING: err while un-registering JmsAppenderStatsMBean for jmsAppender:" + jmsAppender);
				e.printStackTrace();
			}
		}
	}

	private static final ObjectName toObjectName(final JmsAppender<?> jmsAppender) throws MalformedObjectNameException {
		final ObjectName mbeanName = new ObjectName(JmsAppenderStatsMBean.class.getPackage().getName() + ":type="
				+ JmsAppenderStats.class.getName() + ",name=" + jmsAppender.getId());
		return mbeanName;
	}
}
