# Logback JMS Appender
Provides a way to send all your logback messages over JMS to a queue! Take advantage of reliable messaging and buffering already built into your app and use it for logging.

This plugin performs a JNDI lookup to a JMS connection factory. It requires at least Java EE 7. 

If you're using ActiveMQ and Graylog, consider taking a look at my ActiveMQ Input Plugin to bridge ActiveMQ->Graylog. https://github.com/exabrial/graylog-plugin-openwire

## License
All files are Licensed Apache Source License 2.0. Please consider contributing back any changes you may make, thanks!

## Usage

The default `com.github.exabrial.logback.JmsAppender` will work out-of-the-box with Apache TomEE/OpenEJB and send logging events to the `ch.qos.logback` queue.

