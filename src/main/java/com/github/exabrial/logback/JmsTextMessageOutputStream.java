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
	private final String initialContextFactory;
	private final String jmsConnectionFactoryJndiName;
	private final String queueName;
	private Queue queue;
	private JMSProducer producer;
	private JMSContext jmsContext;
	private Writer writer;
	private String state = "uninitialized";
	private InitialContext context;

	public JmsTextMessageOutputStream(String initialContextFactory, String jmsConnectionFactoryJndiName, String queueName)
			throws NamingException {
		this.initialContextFactory = initialContextFactory;
		this.jmsConnectionFactoryJndiName = jmsConnectionFactoryJndiName;
		this.queueName = queueName;
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
		context = new InitialContext(props);
		ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(jmsConnectionFactoryJndiName);
		jmsContext = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
		queue = jmsContext.createQueue(queueName);
		producer = jmsContext.createProducer();
		state = "initialized";
	}

	@Override
	public void write(int out) throws IOException {
		if (writer == null) {
			state = "writing";
			writer = new StringWriter();
		}
		writer.write(out);
	}

	@Override
	public void flush() throws IOException {
		state = "flushing";
		final String buffer = writer.toString();
		writer = null;
		producer.send(queue, buffer);
		state = "initialized";
	}

	@Override
	public void close() throws IOException {
		state = "closing";
		try {
			context.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				jmsContext.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				context = null;
				jmsContext = null;
				queue = null;
				producer = null;
				writer = null;
				state = "closed";
			}
		}
	}

	@Override
	public String toString() {
		return "JmsTextMessageOutputStream [state=" + state + ", initialContextFactory=" + initialContextFactory
				+ ", jmsConnectionFactoryJndiName=" + jmsConnectionFactoryJndiName + ", queueName=" + queueName + "]";
	}
}
