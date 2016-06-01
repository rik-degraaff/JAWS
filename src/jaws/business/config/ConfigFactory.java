package jaws.business.config;

import java.util.Properties;
import java.util.Map.Entry;

import org.json.JSONObject;

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

	public static void saveConfig(String configName, Properties config) {

		if(ConfigLoader.configExists(configName)) {
			ConfigLoader.saveConfig(configName, mergeProperties(ConfigLoader.loadConfig(configName), config));
		} else {
			ConfigLoader.saveConfig(configName, config);
		}
	}

	public static Properties mergeProperties(Properties mainProperties, Properties... otherProperties) {

		Properties result = new Properties(mainProperties);

		for(Properties properties : otherProperties) {
			result.putAll(properties);
		}

		return result;
	}

	public static JSONObject configToJSON(Properties config) {

		JSONObject configJSON = new JSONObject();
		for(Entry<Object, Object> configEntry : config.entrySet()) {
			configJSON.put((String) configEntry.getKey(), configEntry.getValue());
		}

		return configJSON;
	}
}
