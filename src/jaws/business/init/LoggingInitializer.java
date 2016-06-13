package jaws.business.init;

import java.util.Properties;

import jal.business.log.JALogger;
import jal.business.log.LogLevel;
import jal.business.loggers.FileLogger;
import jal.business.loggers.StreamLogger;
import jaws.business.config.ConfigFactory;
import jaws.context.Context;
import jaws.util.Box;

/**
 * A static initializer class to prepare the logging system.
 * 
 * @author Roy
 *
 */
public final class LoggingInitializer {

	private static final String configName = "logs";

	private static boolean initialized = false;

	private LoggingInitializer() {}

	public static boolean initialized() {

		return initialized;
	}

	public static void init(Box<JALogger> loggerBox) {

		if(initialized) {
			throw new IllegalStateException("LoggingInitializer already initialized");
		}

		Properties properties = ConfigFactory.getConfig(configName);

		LogLevel logLevel;
		try {
			logLevel = LogLevel.valueOf(properties.getProperty("loglevel"));
		} catch(IllegalArgumentException e) {
			logLevel = LogLevel.INFO;
		}

		JALogger logger = new JALogger(logLevel);
		logger.addListener(new StreamLogger(System.out));
		logger.addListener(new FileLogger(properties.getProperty("log_folder") + "/jaws.log"));
		Context.logger = logger;
		loggerBox.box(logger);

		Context.logger.info("LoggingInitializer initialized.");

		initialized = true;
	}

	public static void deinit() {

		if(!initialized) {
			throw new IllegalStateException("LoggingInitializer not yet initialized");
		}

		Context.logger = null;

		initialized = false;
	}
}
