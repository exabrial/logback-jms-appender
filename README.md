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
  <version>1.1.0</version>
  <scope>runtime</scope>
</dependency>
```

If you're logging to Graylog I suggest you also use a GELF encoder:

```
<dependency>
  <groupId>de.siegmar</groupId>
  <artifactId>logback-gelf</artifactId>
  <version>3.0.0</version>
  <scope>runtime</scope>
</dependency>
```

## Configuration


| Property Name                | Default                                              | Purpose                                                                                                                         |
|------------------------------|------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| initialContextFactory        | org.apache.openejb.client.LocalInitialContextFactory | Each Java EE server will have a different InitialContext class to use. You'll find this in your server's documentation.         |
| jmsConnectionFactoryJndiName | openejb:Resource/jms/connectionFactory | This is passed to the initial context factory to perform the lookup. Different servers will keep resources in different places. |
| queueName                    | ch.qos.logback | The JMS Queue name to send messages to.                                                                                         |
| async                        | false | Use an ArrayBlockingQueue as a buffer. Messages will be sent in another thread. Message overflows are dumped to `System.err` after waiting 25ms. A JMX is registered to monitor stats. While the default here is `false` for backwards compatibility, it's highly recommend you set this to `true`. |
| asyncBufferSize             | 256 | If `async=true`, this is size of the ArrayBlockingQueue. It is recommended to leave this as is. Monitor JMX for writeStalls and messagesDropped if changed. |

### Example Configuration

This configuration uses the GELF encoder mentioned earlier (since I send my logging events to Graylog), but any Logback encoder could be used. Note you should replace `${project.artifactId}` below:

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
	<contextName>your-app-name</contextName>
	<jmxConfigurator />
	<appender
		name="gelf-jms"
		class="com.github.exabrial.logback.JmsAppender">
		<async>true</async>
		<queueName>com.emoneyusa.logback</queueName>
		<encoder class="de.siegmar.logbackgelf.GelfEncoder">
			<includeCallerData>true</includeCallerData>
			<includeRootCauseData>true</includeRootCauseData>
			<includeLevelName>true</includeLevelName>
			<shortPatternLayout class="ch.qos.logback.classic.PatternLayout">
				<pattern>%.100ex{short}%.100m</pattern>
			</shortPatternLayout>
			<fullPatternLayout class="ch.qos.logback.classic.PatternLayout">
				<pattern>%msg</pattern>
			</fullPatternLayout>
			<staticField>app:${project.artifactId}</staticField>
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
