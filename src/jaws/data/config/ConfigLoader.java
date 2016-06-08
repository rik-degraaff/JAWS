package jaws.data.config;

import static trycrash.Try.tryCatch;
import static trycrash.Try.tryCrash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;

import jaws.context.Context;

/**
 * A static class that allows configs to be save locally.
 * 
 * @author Rik
 * 
 * @see jaws.business.config.ConfigFactory
 * @see jaws.business.config.ConfigRequestProcessor
 * @see jaws.business.config.DefaultConfigs
 */
final public class ConfigLoader {

	private static String configFolder;
	private static final String fileExtension = ".properties";

	private static boolean initialized = false;

	private ConfigLoader() {}
	
	/**
	 * @return {@code true} if the ConfigLoader has already been initialized.
	 */
	public static boolean initialized() {
		return initialized;
	}
	
	/**
	 * Initializes the ConfigLoader to save its configs in a certain folder.
	 * 
	 * @param configFolder the folder to save the configs in.
	 */
	public static void init(String configFolder) {

		if(initialized) {
			throw new IllegalStateException("ConfigLoader already inititalized");
		}

		ConfigLoader.configFolder = configFolder;

		initialized = true;
	}
	
	/**
	 * deinitializes the ConfigLoader.
	 */
	public static void deinit() {

		if(!initialized) {
			throw new IllegalStateException("ConfigLoader not yet initialized");
		}

		configFolder = null;

		initialized = false;
	}
	
	/**
	 * Checks if a given config exists.
	 * 
	 * @param configName the name of the config.
	 * 
	 * @return {@code true} if the config exists.
	 */
	public static boolean configExists(String configName) {

		return Files.exists(new File(getConfigFileName(configName)).toPath());
	}

	/**
	 * Loads a given config from the filesystem.
	 * 
	 * @param configName the name of the config.
	 * 
	 * @return the loaded config.
	 */
	public static Properties loadConfig(String configName) {

		return tryCatch(() -> {

			Properties properties = new Properties();
			InputStream inputStream = new FileInputStream(getConfigFileName(configName));

			if(inputStream != null) {
				try {
					properties.load(inputStream);
				} catch (IOException e) {
					throw new RuntimeException("Error loading config file", e);
				}
			}

			return properties;
		}).get();
	}
	
	/**
	 * Saves a given config to the filesystem.
	 * 
	 * @param configName the name of the config.
	 * @param properties the config to save.
	 */
	public static void saveConfig(String configName, Properties properties) {

		File file = new File(getConfigFileName(configName));
		file.getParentFile().mkdirs();
		tryCrash(() -> {
			file.createNewFile();
			OutputStream out = new FileOutputStream(file);
			properties.store(out, "");
			out.close();
			if(Context.logger == null) {
				System.out.println("Config file was not found, created a default at: " + file.getCanonicalPath());
			} else {
				Context.logger.info("Config file was not found, created a default at: " + file.getCanonicalPath());
			}
		});
	}

	private static String getConfigFileName(String configName) {

		return configFolder + configName + fileExtension;
	}
}
