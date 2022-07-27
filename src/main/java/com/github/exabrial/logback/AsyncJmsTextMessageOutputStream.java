package com.github.exabrial.logback;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class AsyncJmsTextMessageOutputStream extends OutputStream {
	private ArrayBlockingQueue<byte[]> messageBuffer = new ArrayBlockingQueue<>(256);
	private Thread outputThread;
	private final AtomicLong messagesDequeued = new AtomicLong(0);
	private final AtomicInteger messagedDropped = new AtomicInteger(0);
	private final AtomicInteger writeStalls = new AtomicInteger(0);

	public AsyncJmsTextMessageOutputStream(final String initialContextFactory, final String jmsConnectionFactoryJndiName,
			final String queueName) throws NamingException {
		this.outputThread = new Thread() {
			@Override
			public void run() {
				InitialContext initialContext = null;
				JMSContext jmsContext = null;
				try {
					final Properties props = new Properties();
					props.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
					initialContext = new InitialContext(props);
					final ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup(jmsConnectionFactoryJndiName);
					try {
						if (initialContext != null) {
							initialContext.close();
						}
					} catch (final NamingException e) {
						// toss
					}
					jmsContext = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
					jmsContext.start();
					final Queue jmsQueue = jmsContext.createQueue(queueName);
					final JMSProducer jmsProducer = jmsContext.createProducer();
					while (!Thread.interrupted()) {
						final byte[] buffer = messageBuffer.take();
						final String message;
						if (buffer != null && buffer.length > 0) {
							message = new String(buffer);
							messagesDequeued.incrementAndGet();
							try {
								jmsProducer.send(jmsQueue, message);
							} catch (final Exception e) {
								System.err
										.println("WARNING: Failed to send message over JMS. JmsTextMessageOutputStream is dropping message:" + message);
								e.printStackTrace();
								messagedDropped.incrementAndGet();
							}
						}
					}
				} catch (final InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (final Exception e) {
					System.err.println("Could not initialize JmsTextMessageOutputStream!");
					e.printStackTrace();
					throw new RuntimeException(e);
				} finally {
					if (jmsContext != null) {
						jmsContext.close();
					}
				}
			}
		};
		outputThread.setName("JmsTextMessageOutputStream - outputThread");
		outputThread.start();
	}

	@Override
	public void write(final int b) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(final byte[] directWriteBuffer) throws IOException {
		if (!messageBuffer.offer(directWriteBuffer)) {
			writeStalls.incrementAndGet();
			try {
				if (!messageBuffer.offer(directWriteBuffer, 25, TimeUnit.MILLISECONDS)) {
					messagedDropped.incrementAndGet();
					System.err.println("WARNING: Buffer full after waiting 25ms. JmsTextMessageOutputStream is dropping message:"
							+ new String(directWriteBuffer));
				}
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void flush() throws IOException {
		// party
	}

	@Override
	public void close() throws IOException {
		try {
			outputThread.interrupt();
		} finally {
			messageBuffer.clear();
			messageBuffer = null;
			outputThread = null;
		}
	}

	public int getCurrentQueueDepth() {
		return messageBuffer.size();
	}

	public long getMessagesDequeued() {
		return messagesDequeued.get();
	}

	public int getMessagedDropped() {
		return messagedDropped.get();
	}

	public int getWriteStalls() {
		return writeStalls.get();
	}
}
