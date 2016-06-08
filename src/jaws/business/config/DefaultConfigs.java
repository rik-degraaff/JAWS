package jaws.business.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * A static class that contains all default configs.
 * 
 * @author Roy
 * 
 * @see jaws.data.config.ConfigLoader
 * @see jaws.business.config.ConfigRequestProcessor
 * @see jaws.business.config.ConfigFactory
 */
final class DefaultConfigs {

	private static Map<String, Properties> defaultProperties;
	
	private DefaultConfigs() {}

	static {
		defaultProperties = new HashMap<>();

		// logs
		Properties logs = new Properties();
		logs.setProperty("log_folder", "../logs");
		logs.setProperty("loglevel", "INFO");
		defaultProperties.put("logs", logs);

		// web
		Properties web = new Properties();
		web.setProperty("module_folder", "../modules");
		web.setProperty("webroot", "../www");
		web.setProperty("port", "80");
		defaultProperties.put("web", web);

		// configClient
		Properties configClient = new Properties();
		configClient.setProperty("port", "8080");
		defaultProperties.put("configClient", configClient);
	}
	
	/**
	 * Gets a default config by name. Will return an empty config if no default exists.
	 * 
	 * @param configName the name of the config.
	 * 
	 * @return the config.
	 */
	public static Properties getDefaultConfig(String configName) {

		Properties properties;
		if((properties = defaultProperties.get(configName)) != null) {
			return properties;
		} else {
			return new Properties();
		}
	}
	
	/**
	 * gets the names of all configs that have default values, which are all that are expected to be used.
	 * 
	 * @return a set of containing all names.
	 */
	public static Set<String> getConfigNames() {

		return defaultProperties.keySet();
	}
}
