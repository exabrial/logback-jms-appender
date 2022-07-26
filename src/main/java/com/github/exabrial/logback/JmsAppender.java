package com.github.exabrial.logback;

import ch.qos.logback.core.OutputStreamAppender;

public class JmsAppender<E> extends OutputStreamAppender<E> {
	private String initialContextFactory = "org.apache.openejb.client.LocalInitialContextFactory";
	private String jmsConnectionFactoryJndiName = "openejb:Resource/jms/connectionFactory";
	private String queueName = "ch.qos.logback";
	private boolean async = false;

	@Override
	public void start() {
		if (isStarted()) {
			return;
		} else {
			try {
				if (!async) {
					setOutputStream(new JmsTextMessageOutputStream(initialContextFactory, jmsConnectionFactoryJndiName, queueName));
				} else {
					setOutputStream(new AsyncJmsTextMessageOutputStream(initialContextFactory, jmsConnectionFactoryJndiName, queueName));
				}
				super.start();
			} catch (final Exception e) {
				addError("start() caught exception!", e);
			}
		}
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(final boolean async) {
		this.async = async;
	}

	public String getInitialContextFactory() {
		return initialContextFactory;
	}

	public void setInitialContextFactory(final String initialContextFactory) {
		this.initialContextFactory = initialContextFactory;
	}

	public String getJmsConnectionFactoryJndiName() {
		return jmsConnectionFactoryJndiName;
	}

	public void setJmsConnectionFactoryJndiName(final String jmsConnectionFactoryJndiName) {
		this.jmsConnectionFactoryJndiName = jmsConnectionFactoryJndiName;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(final String queueName) {
		this.queueName = queueName;
	}

	@Override
	public void setImmediateFlush(final boolean immediateFlush) {
		super.setImmediateFlush(true);
	}

	public int getQueueDepth() {
		if (getOutputStream() instanceof AsyncJmsTextMessageOutputStream) {
			return ((AsyncJmsTextMessageOutputStream) getOutputStream()).getQueueDepth();
		} else {
			return -1;
		}
	}

	public long getMessagesDequeued() {
		if (getOutputStream() instanceof AsyncJmsTextMessageOutputStream) {
			return ((AsyncJmsTextMessageOutputStream) getOutputStream()).getMessagesDequeued();
		} else {
			return -1;
		}
	}

	public int getMessagedDropped() {
		if (getOutputStream() instanceof AsyncJmsTextMessageOutputStream) {
			return ((AsyncJmsTextMessageOutputStream) getOutputStream()).getMessagedDropped();
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return "JmsAppender [initialContextFactory=" + initialContextFactory + ", jmsConnectionFactoryJndiName="
				+ jmsConnectionFactoryJndiName + ", queueName=" + queueName + ", async=" + async + ", getQueueDepth()=" + getQueueDepth()
				+ ", getMessagesDequeued()=" + getMessagesDequeued() + ", getMessagedDropped()=" + getMessagedDropped() + "]";
	}
}
