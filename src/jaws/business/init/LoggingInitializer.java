package jaws.business.init;

import jal.business.log.JALogger;
import jal.business.loggers.FileLogger;
import jal.business.loggers.StreamLogger;
import jaws.context.Context;

public final class LoggingInitializer {

	private static boolean initialized = false;

	private LoggingInitializer() {}

	public void init() {

		if(initialized) {
			throw new IllegalStateException("LoggingInitializer already initialized");
		} else {
			initialized = true;
		}

		JALogger logger = new JALogger();
		logger.addListener(new StreamLogger(System.out));
		logger.addListener(new FileLogger("../logs/jaws.log"));
		Context.logger = logger;
	}

	public void deinit() {

		if(!initialized) {
			throw new IllegalStateException("LoggingInitializer not yet initialized");
		} else {
			initialized = false;
		}

		Context.logger = null;
	}
}
