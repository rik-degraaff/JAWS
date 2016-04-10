package jaws.business.init;

import static trycrash.Try.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import jaws.business.net.RequestProcessor;
import jaws.business.thread.ThreadPool;
import jaws.data.module.ModuleLoader;
import jaws.data.net.PortListener;

public final class WebInitializer {

	private static final String webConfigFile = "web.properties";

	private static boolean initialized = false;
	private static ThreadPool threadPool;

	private WebInitializer() {}

	public static void init(String fileLocation) {

		if(initialized) {
			throw new IllegalStateException("WebInitializer already initialized");
		} else {
			initialized = true;
		}

		Properties properties = loadConfig(fileLocation + "/" + webConfigFile);

		ModuleLoader.init(properties.getProperty("module_folder"));
		threadPool = new ThreadPool(5);
		
		new Thread(
			new PortListener(Integer.parseInt(properties.getProperty("port")), client -> {
				final RequestProcessor handler = new RequestProcessor(ModuleLoader.getHandlerGetter(), properties.getProperty("webroot"));
				tryCatch(() -> threadPool.execute(() -> handler.handle(client)));
			})
		).start();
	}

	private static Properties loadConfig(String fileLocation) { //TODO move to data layer

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
			
			Properties p = new Properties();
			p.setProperty("module_folder", "../modules");
			p.setProperty("webroot", "../www");
			p.setProperty("port", "80");
			File f = new File(fileLocation);
			f.getParentFile().mkdirs();
			tryCrash(() -> {
				f.createNewFile();
				OutputStream out = new FileOutputStream(f);
				p.store(out, "");
				out.close();
				System.out.println("Config file was not found, created a default at: " + f.getCanonicalPath());
			});
			return p;
		});
	}
}
