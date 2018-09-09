# Logback JMS Appender
Provides a way to send all your logback messages over JMS to a queue! Take advantage of reliable messaging and buffering already built into your app and use it for logging. Bridges `YourApp->ActiveMQ` (or your server's JMS provider)

This plugin performs a JNDI lookup to a JMS connection factory. It requires at least Java EE 7. 

If you're using ActiveMQ and Graylog, consider taking a look at my ActiveMQ Input Plugin to bridge ActiveMQ->Graylog. https://github.com/exabrial/graylog-plugin-openwire

## License
All files are Licensed Apache Source License 2.0. Please consider contributing back any changes you may make, thanks!

## Usage

The default `com.github.exabrial.logback.JmsAppender` will work almost-out-of-the-box with Apache TomEE/OpenEJB and send logging events to the `ch.qos.logback` queue. It will attempt to lookup a jms connection factory named `jms/connectionFactory`, which you should create in your tomee.xml.


### Maven Coordinates

```
<dependency>
  <groupId>com.github.exabrial</groupId>
  <artifactId>logback-jms-appender</artifactId>
  <version>1.0.0</version>
  <scope>runtime</scope>
</dependency>
```

If you're logging to graylog I suggest you also use the GELF encoder from this project: https://github.com/Moocar/logback-gelf

```
<dependency>
  <groupId>me.moocar</groupId>
  <artifactId>logback-gelf</artifactId>
  <version>0.3</version>
</dependency>
```

## Configuration


| Property Name                | Default                                              | Purpose                                                                                                                         | Required |
|------------------------------|------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|----------|
| initialContextFactory        | org.apache.openejb.client.LocalInitialContextFactory | Each Java EE server will have a different InitialContext class to use. You'll find this in your server's documentation.         | Y        |
| jmsConnectionFactoryJndiName | openejb:Resource/jms/connectionFactory               | This is passed to the initial context factory to perform the lookup. Different servers will keep resources in different places. | Y        |
| queueName                    | ch.qos.logback                                       | The JMS Queue name to send messages to.                                                                                         | Y        |

### Example Configuration

This configuration uses the GELF encoder mentioned earlier (since I send my logging events to Graylog), but any Logback encoder could be used.

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
	<contextName>your-app-name</contextName>
	<jmxConfigurator />
	<appender
		name="gelf-jms"
		class="com.github.exabrial.logback.JmsAppender">
		<queueName>com.example.logback</queueName>
		<encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
			<layout class="me.moocar.logbackgelf.GelfLayout">
				<useLoggerName>true</useLoggerName>
				<useThreadName>true</useThreadName>
				<includeFullMDC>true</includeFullMDC>
				<staticField class="me.moocar.logbackgelf.Field">
					<key>_app</key>
					<value>your-app-name</value>
				</staticField>
			</layout>
		</encoder>
	</appender>
	<root level="info">
		<appender-ref ref="gelf-jms" />
	</root>
	<logger
		name="com.example"
		level="debug" />
</configuration>
```
