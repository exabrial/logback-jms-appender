package com.github.exabrial.logback;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JmsTextMessageOutputStream extends OutputStream {
	private Queue jmsQueue;
	private JMSProducer jmsProducer;
	private JMSContext jmsContext;
	private Writer writer;
	private InitialContext initialContext;

	public JmsTextMessageOutputStream(final String initialContextFactory, final String jmsConnectionFactoryJndiName,
			final String queueName) throws NamingException {
		final Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
		initialContext = new InitialContext(props);
		final ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup(jmsConnectionFactoryJndiName);
		jmsContext = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
		jmsQueue = jmsContext.createQueue(queueName);
		jmsProducer = jmsContext.createProducer();
	}

	@Override
	public void write(final int out) throws IOException {
		if (writer == null) {
			writer = new StringWriter();
		}
		writer.write(out);
	}

	@Override
	public void write(final byte[] b) throws IOException {
		if (writer == null) {
			writer = new StringWriter();
		}
		writer.write(new String(b));
	}

	@Override
	public void flush() throws IOException {
		final String buffer = writer.toString();
		writer = null;
		jmsProducer.send(jmsQueue, buffer);
	}

	@Override
	public void close() throws IOException {
		try {
			initialContext.close();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				jmsContext.close();
			} catch (final Exception e) {
				throw new RuntimeException(e);
			} finally {
				initialContext = null;
				jmsContext = null;
				jmsQueue = null;
				jmsProducer = null;
				writer = null;
			}
		}
	}
}
