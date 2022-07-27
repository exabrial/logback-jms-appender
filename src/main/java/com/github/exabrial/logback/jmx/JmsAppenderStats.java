package com.github.exabrial.logback.jmx;

import com.github.exabrial.logback.JmsAppender;

public class JmsAppenderStats implements JmsAppenderStatsMBean {
	private final JmsAppender<?> jmsAppender;

	public JmsAppenderStats(final JmsAppender<?> jmsAppender) {
		this.jmsAppender = jmsAppender;
	}

	@Override
	public String getId() {
		return jmsAppender.getId();
	}

	@Override
	public boolean isAsync() {
		return jmsAppender.isAsync();
	}

	@Override
	public String getJmsConnectionFactoryJndiName() {
		return jmsAppender.getJmsConnectionFactoryJndiName();
	}

	@Override
	public String getQueueName() {
		return jmsAppender.getQueueName();
	}

	@Override
	public int getAsyncBufferSize() {
		return jmsAppender.getAsyncBufferSize();
	}

	@Override
	public boolean isStarted() {
		return jmsAppender.isStarted();
	}

	@Override
	public boolean isImmediateFlush() {
		return jmsAppender.isImmediateFlush();
	}

	@Override
	public int getCurrentQueueDepth() {
		return jmsAppender.getCurrentQueueDepth();
	}

	@Override
	public long getMessagesDequeued() {
		return jmsAppender.getMessagesDequeued();
	}

	@Override
	public int getMessagesDropped() {
		return jmsAppender.getMessagesDropped();
	}

	@Override
	public int getWriteStalls() {
		return jmsAppender.getWriteStalls();
	}
}
