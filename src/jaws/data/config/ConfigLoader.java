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

public class ConfigLoader {

	private static String configFolder;
	private static final String fileExtension = ".properties";

	private static boolean initialized = false;

	private ConfigLoader() {}

	public static boolean initialized() {
		return initialized;
	}

	public static void init(String configFolder) {

		if(initialized) {
			throw new IllegalStateException("ConfigLoader already inititalized");
		}

		ConfigLoader.configFolder = configFolder;

		initialized = true;
	}

	public static void deinit() {

		if(!initialized) {
			throw new IllegalStateException("ConfigLoader not yet initialized");
		}

		configFolder = null;

		initialized = false;
	}

	public static boolean configExists(String configName) {

		return Files.exists(new File(getConfigFileName(configName)).toPath());
	}

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
