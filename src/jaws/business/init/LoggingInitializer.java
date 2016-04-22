package jaws.business.init;

import java.util.Properties;

import jal.business.log.JALogger;
import jal.business.log.LogLevel;
import jal.business.loggers.FileLogger;
import jal.business.loggers.StreamLogger;
import jaws.context.Context;

public final class LoggingInitializer {
	
	private static final String logConfigFile = "logs.properties";

	private static boolean initialized = false;

	private LoggingInitializer() {}

	public static boolean initialized() {
		
		return initialized;
	}

	public static void init(String fileLocation) {

		if(initialized) {
			throw new IllegalStateException("LoggingInitializer already initialized");
		}
		
		Properties defaultProperties = new Properties();
		defaultProperties.setProperty("loglevel", "INFO");

		Properties properties = WebInitializer.loadConfig(fileLocation + "/" + logConfigFile, defaultProperties); //TODO when moved, to data layer update this too

		LogLevel logLevel;
		try {
			logLevel = LogLevel.valueOf(properties.getProperty("loglevel"));
		} catch(IllegalArgumentException e) {
			logLevel = LogLevel.INFO;
		}
		
		JALogger logger = new JALogger(logLevel);
		logger.addListener(new StreamLogger(System.out));
		logger.addListener(new FileLogger("../logs/jaws.log"));
		Context.logger = logger;
		
		Context.logger.info("LoggingInitializer initialized.");
		
		initialized = true;
	}

	public static void deinit() {

		if(!initialized) {
			throw new IllegalStateException("LoggingInitializer not yet initialized");
		} else {
			initialized = false;
		}

		Context.logger = null;
	}
}
