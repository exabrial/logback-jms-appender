package com.github.exabrial.logback.jmx;

public interface JmsAppenderStatsMBean {
	String getId();

	boolean isAsync();

	String getJmsConnectionFactoryJndiName();

	String getQueueName();

	int getAsyncBufferSize();

	boolean isStarted();

	boolean isImmediateFlush();

	int getCurrentQueueDepth();

	long getMessagesDequeued();

	int getMessagesDropped();

	int getWriteStalls();
}
