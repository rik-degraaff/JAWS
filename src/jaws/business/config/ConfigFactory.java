package jaws.business.config;

import java.util.Properties;

import jaws.data.config.ConfigLoader;

public class ConfigFactory {

	public static Properties getConfig(String configName) {

		Properties defaultProperties = DefaultConfigs.getDefaultConfig(configName);

		if(ConfigLoader.configExists(configName)) {
			return mergeProperties(defaultProperties, ConfigLoader.loadConfig(configName));
		} else {
			ConfigLoader.saveConfig(configName, defaultProperties);
			return defaultProperties;
		}
	}

	public static Properties mergeProperties(Properties mainProperties, Properties... otherProperties) {

		Properties result = new Properties(mainProperties);

		for(Properties properties : otherProperties) {
			result.putAll(properties);
		}

		return result;
	}
}
