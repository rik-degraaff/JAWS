package jaws.business.init;

import java.util.Optional;
import java.util.Properties;

import jal.business.log.JALogger;
import jaws.business.config.ConfigFactory;
import jaws.business.config.ConfigRequestProcessor;
import jaws.business.loggers.LogCache;
import jaws.data.net.PortListener;
import jaws.util.thread.StoppableThread;

/**
 * A static initializer class to prepare the server for the communication with the configuration client.
 * 
 * @author Roy
 *
 */
public class ConfigClientInitializer {

	private static final String configName = "configClient";

	private static boolean initialized = false;
	private static StoppableThread portListenerThread;
	private static LogCache logCache;

	private ConfigClientInitializer() {}

	public static boolean initialized() {
		return initialized;
	}

	public static void init(Optional<JALogger> logger) {

		if(initialized) {
			throw new IllegalStateException("ConfigClientInitializer already initialized");
		}

		logCache = new LogCache(100);
		logger.ifPresent(l -> l.addListener(logCache));

		Properties properties = ConfigFactory.getConfig(configName);

		portListenerThread = new StoppableThread(
			new PortListener(Integer.parseInt(properties.getProperty("port")), client -> {

				new ConfigRequestProcessor(logCache).handle(client);
			})
		);
		portListenerThread.start();

		initialized = true;
	}

	public static void deinit() {

		if(!initialized) {
			throw new IllegalStateException("ConfigClientInitializer not yet initialized");
		}

		portListenerThread.interrupt();
		portListenerThread = null;

		initialized = false;
	}
}
