package com.github.exabrial.logback;

import ch.qos.logback.core.OutputStreamAppender;

public class JmsAppender<E> extends OutputStreamAppender<E> {
	private String initialContextFactory = "org.apache.openejb.client.LocalInitialContextFactory";
	private String jmsConnectionFactoryJndiName = "openejb:Resource/jms/connectionFactory";
	private String queueName = "ch.qos.logback";

	@Override
	public void start() {
		if (isStarted()) {
			return;
		} else {
			try {
				setOutputStream(new JmsTextMessageOutputStream(initialContextFactory, jmsConnectionFactoryJndiName, queueName));
				super.start();
			} catch (Exception e) {
				addError("start() caught exception!", e);
			}
		}
	}

	public String getInitialContextFactory() {
		return initialContextFactory;
	}

	public void setInitialContextFactory(String initialContextFactory) {
		this.initialContextFactory = initialContextFactory;
	}

	public String getJmsConnectionFactoryJndiName() {
		return jmsConnectionFactoryJndiName;
	}

	public void setJmsConnectionFactoryJndiName(String jmsConnectionFactoryJndiName) {
		this.jmsConnectionFactoryJndiName = jmsConnectionFactoryJndiName;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	@Override
	public String toString() {
		return "JmsAppender [initialContextFactory=" + initialContextFactory + ", jmsConnectionFactoryJndiName="
				+ jmsConnectionFactoryJndiName + ", queueName=" + queueName + "]";
	}
}
