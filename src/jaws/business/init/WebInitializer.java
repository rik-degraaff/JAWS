package jaws.business.init;

import static trycrash.Try.tryCatch;
import static trycrash.Try.tryCrash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import jaws.business.net.RequestProcessor;
import jaws.business.thread.StoppableThread;
import jaws.business.thread.ThreadPool;
import jaws.context.Context;
import jaws.data.module.ModuleLoader;
import jaws.data.net.PortListener;

public final class WebInitializer {

	private static final String webConfigFile = "jaws.properties";

	private static boolean initialized = false;
	private static ThreadPool threadPool;
	private static StoppableThread portListenerThread;

	private WebInitializer() {}
	
	public static boolean initialized() {
		
		return initialized;
	}

	public static void init(String fileLocation) {

		if(initialized) {
			throw new IllegalStateException("WebInitializer already initialized");
		}
		
		Properties defaultProperties = new Properties();
		defaultProperties.setProperty("module_folder", "../modules");
		defaultProperties.setProperty("webroot", "../www");
		defaultProperties.setProperty("port", "80");

		Properties properties = loadConfig(fileLocation + "/" + webConfigFile, defaultProperties);

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
		} else {
			initialized = false;
		}

		portListenerThread.interrupt();
		portListenerThread = null;
		threadPool.stop();
		threadPool = null;
	}

	static Properties loadConfig(String fileLocation, Properties defaultProperties) { //TODO move to data layer

		return tryCatch(() -> {

			Properties properties = new Properties();
			InputStream inputStream = new FileInputStream(fileLocation);

			if(inputStream != null) {
				try {
					properties.load(inputStream);
				} catch (IOException e) {
					throw new RuntimeException("Error loading config file", e);
				}
			}

			return properties;
		}).orElseGet(() -> {

			File file = new File(fileLocation);
			file.getParentFile().mkdirs();
			tryCrash(() -> {
				file.createNewFile();
				OutputStream out = new FileOutputStream(file);
				defaultProperties.store(out, "");
				out.close();
				Context.logger.info("Config file was not found, created a default at: " + file.getCanonicalPath());
			});
			return defaultProperties;
		});
	}
}
