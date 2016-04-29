package jaws.business.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class DefaultConfigs {

	private static Map<String, Properties> defaultProperties;

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

	public static Properties getDefaultConfig(String configName) {

		Properties properties;
		if((properties = defaultProperties.get(configName)) != null) {
			return properties;
		} else {
			return new Properties();
		}
	}
}
