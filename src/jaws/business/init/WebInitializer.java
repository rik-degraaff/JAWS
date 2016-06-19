package jaws.business.init;

import static trycrash.Try.tryCrash;

import java.util.Properties;

import jaws.business.config.ConfigFactory;
import jaws.business.net.RequestProcessor;
import jaws.context.Context;
import jaws.data.module.ModuleLoader;
import jaws.data.net.PortListener;
import jaws.util.thread.StoppableThread;
import jaws.util.thread.ThreadPool;

/**
 * A static initializer class to prepare the HTTP port listening and request processing.
 * 
 * @author Roy
 *
 */
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
				final RequestProcessor processor = new RequestProcessor(ModuleLoader.getHandlerGetter(), properties.getProperty("webroot"));
				tryCrash(() -> threadPool.execute(() -> processor.process(client)));
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
	
	public static void reinit() {
		if(initialized) deinit();
		init();
	}
}
