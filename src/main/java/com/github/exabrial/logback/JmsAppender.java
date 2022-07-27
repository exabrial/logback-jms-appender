package com.github.exabrial.logback;

import java.util.UUID;

import com.github.exabrial.logback.jmx.JmsAppenderJmxCollector;

import ch.qos.logback.core.OutputStreamAppender;

public class JmsAppender<E> extends OutputStreamAppender<E> {
	private String initialContextFactory = "org.apache.openejb.client.LocalInitialContextFactory";
	private String jmsConnectionFactoryJndiName = "openejb:Resource/jms/connectionFactory";
	private String queueName = "ch.qos.logback";
	private boolean async = false;
	private final String id = UUID.randomUUID().toString();

	@Override
	public void start() {
		if (isStarted()) {
			return;
		} else {
			setImmediateFlush(true);
			try {
				if (!async) {
					setOutputStream(new JmsTextMessageOutputStream(initialContextFactory, jmsConnectionFactoryJndiName, queueName));
				} else {
					setOutputStream(new AsyncJmsTextMessageOutputStream(initialContextFactory, jmsConnectionFactoryJndiName, queueName));
				}
				super.start();
				addInfo("JmsAppender using async mode:" + async);
				JmsAppenderJmxCollector.add(this);
			} catch (final Exception e) {
				addError("start() caught exception!", e);
			}
		}
	}

	@Override
	public void stop() {
		JmsAppenderJmxCollector.remove(this);
		super.stop();
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

	public int getCurrentQueueDepth() {
		if (getOutputStream() instanceof AsyncJmsTextMessageOutputStream) {
			return ((AsyncJmsTextMessageOutputStream) getOutputStream()).getCurrentQueueDepth();
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

	public int getWriteStalls() {
		if (getOutputStream() instanceof AsyncJmsTextMessageOutputStream) {
			return ((AsyncJmsTextMessageOutputStream) getOutputStream()).getWriteStalls();
		} else {
			return -1;
		}
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "JmsAppender [initialContextFactory=" + initialContextFactory + ", jmsConnectionFactoryJndiName="
				+ jmsConnectionFactoryJndiName + ", queueName=" + queueName + ", async=" + async + ", id=" + id + ", getCurrentQueueDepth()="
				+ getCurrentQueueDepth() + ", getMessagesDequeued()=" + getMessagesDequeued() + ", getMessagedDropped()="
				+ getMessagedDropped() + ", getWriteStalls()=" + getWriteStalls() + "]";
	}
}
