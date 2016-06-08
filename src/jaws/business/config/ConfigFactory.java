package jaws.business.config;

import java.util.Properties;
import java.util.Map.Entry;

import org.json.JSONObject;

import jaws.data.config.ConfigLoader;

/**
 * A factory to handle configs, which are Property objects.
 * 
 * @author Rik
 *
 * @see jaws.data.config.ConfigLoader
 * @see jaws.business.config.ConfigRequestProcessor
 * @see jaws.business.config.DefaultConfigs
 */
final public class ConfigFactory {
	
	private ConfigFactory() {}
	
	/**
	 * Gets a config by its name, will create the config file if it doesn't exist yet and will merge in any default properties that may exist.
	 * 
	 * @param configName the name of the config.
	 * 
	 * @return the config.
	 */
	public static Properties getConfig(String configName) {

		Properties defaultProperties = DefaultConfigs.getDefaultConfig(configName);

		if(ConfigLoader.configExists(configName)) {
			return mergeProperties(defaultProperties, ConfigLoader.loadConfig(configName));
		} else {
			ConfigLoader.saveConfig(configName, defaultProperties);
			return defaultProperties;
		}
	}

	/**
	 * Saves a config under a certain name.
	 * 
	 * @param configName the name of the config.
	 * @param config the config.
	 */
	public static void saveConfig(String configName, Properties config) {

		if(ConfigLoader.configExists(configName)) {
			ConfigLoader.saveConfig(configName, mergeProperties(ConfigLoader.loadConfig(configName), config));
		} else {
			ConfigLoader.saveConfig(configName, config);
		}
	}
	
	/**
	 * Merges two or more properties together, where the first properties will be overwritten by the later ones in case of overlaps.
	 * 
	 * @param mainProperties the basis properties.
	 * @param otherProperties the properties to merge into the basis.
	 * 
	 * @return the merged properties.
	 */
	public static Properties mergeProperties(Properties mainProperties, Properties... otherProperties) {

		Properties result = new Properties(mainProperties);

		for(Properties properties : otherProperties) {
			result.putAll(properties);
		}

		return result;
	}
	
	/**
	 * Converts a config to a JSON object. Each key-value pair becomes a JSOn key-value pair (Strings).
	 * 
	 * @param config the config to convert.
	 * 
	 * @return the config as a JSON object.
	 */
	public static JSONObject configToJSON(Properties config) {

		JSONObject configJSON = new JSONObject();
		for(Entry<Object, Object> configEntry : config.entrySet()) {
			configJSON.put((String) configEntry.getKey(), configEntry.getValue());
		}

		return configJSON;
	}
}
