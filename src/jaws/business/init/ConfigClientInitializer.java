package jaws.business.init;

import java.util.Properties;

import jaws.business.config.ConfigFactory;
import jaws.business.thread.StoppableThread;
import jaws.data.net.PortListener;

public class ConfigClientInitializer {

	private static final String configName = "configClient";

	private static boolean initialized = false;
	private static StoppableThread portListenerThread;

	private ConfigClientInitializer() {}

	public static boolean initialized() {
		return initialized;
	}

	public static void init() {

		if(initialized) {
			throw new IllegalStateException("ConfigClientInitializer already initialized");
		}

		Properties properties = ConfigFactory.getConfig(configName);

		portListenerThread = new StoppableThread(
			new PortListener(Integer.parseInt(properties.getProperty("port")), client -> {

				//TODO handle configclient requests
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
