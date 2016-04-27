package jaws.business.init;

import static trycrash.Try.tryCrash;

import java.util.Properties;

import jaws.business.config.ConfigFactory;
import jaws.business.net.RequestProcessor;
import jaws.business.thread.StoppableThread;
import jaws.business.thread.ThreadPool;
import jaws.context.Context;
import jaws.data.module.ModuleLoader;
import jaws.data.net.PortListener;

public final class WebInitializer {

	private static final String configName = "web";

	private static boolean initialized = false;
	private static ThreadPool threadPool;
	private static StoppableThread portListenerThread;

	private WebInitializer() {}

	public static boolean initialized() {

		return initialized;
	}

	public static void init() {

		if(initialized) {
			throw new IllegalStateException("WebInitializer already initialized");
		}

		Properties properties = ConfigFactory.getConfig(configName);

		ModuleLoader.init(properties.getProperty("module_folder"));
		threadPool = new ThreadPool(5);

		portListenerThread = new StoppableThread(
			new PortListener(Integer.parseInt(properties.getProperty("port")), client -> {

				Context.logger.debug("thread" + Thread.currentThread().getId() + ": processing a connection.");
				final RequestProcessor handler = new RequestProcessor(ModuleLoader.getHandlerGetter(), properties.getProperty("webroot"));
				tryCrash(() -> threadPool.execute(() -> handler.handle(client)));
			})
		);
		portListenerThread.start();

		Context.logger.info("WebInitializer initialized.");
		initialized = true;
	}

	public static void deinit() {

		if(!initialized) {
			throw new IllegalStateException("WebInitializer not yet initialized");
		}

		portListenerThread.interrupt();
		portListenerThread = null;
		threadPool.stop();
		threadPool = null;

		initialized = false;
	}
}
