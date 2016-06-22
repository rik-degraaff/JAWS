package jaws.business.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import jal.business.log.Log;
import jaws.business.init.WebInitializer;
import jaws.business.loggers.LogAccessor;
import jaws.data.net.Connection;

/**
 * A class that can handle a request from the JAWS client.
 * 
 * @author Rik
 */
public class ConfigRequestProcessor {

	private LogAccessor logCache;
	
	/**
	 * Creates a ConfigRequestProcessor with a log accessor to get logs from.
	 * 
	 * @param logCache
	 */
	public ConfigRequestProcessor(LogAccessor logCache) {

		this.logCache = logCache;
	}
	
	/**
	 * Handles a connection. <br>
	 * <br>
	 * Will update the configs, send back the current configs, send back logs since a certain point in time and turn the server on or off if requested. <br>
	 * The result gets written back into the client object.
	 * 
	 * @param client the connection to handle.
	 */
	public void handle(Connection client) {

		JSONObject response = new JSONObject();

		try {
			StringBuilder stringBuilder = new StringBuilder();

			{
				String line;
				BufferedReader reader = client.read();
				System.out.println("start reading from socket");
				while(!(line = reader.readLine()).equals("EndOfMessage")) {
					stringBuilder.append(line);
				}
				System.out.println("finished reading from socket");
			}

			JSONObject request = new JSONObject(stringBuilder.toString());

			if(request.has("updateLogs")) {
				JSONObject logUpdate = new JSONObject();
				response.put("logUpdate", logUpdate);

				JSONObject updateLogs = request.getJSONObject("updateLogs");
				Date lastUpdate;
				List<Log> newLogs;
				Date newLastUpdate;

				if(updateLogs.has("lastUpdate")) {
					lastUpdate = new Date(updateLogs.getLong("lastUpdate"));
					newLogs = logCache.getLogsSince(lastUpdate);
					newLastUpdate = newLogs.stream()
                                           .map(Log::getTimeStamp)
                                           .max(Date::compareTo).orElse(lastUpdate);
				} else {
					Date currentDate = new Date();
					newLogs = logCache.getLogs();
					newLastUpdate = newLogs.stream()
                                           .map(Log::getTimeStamp)
                                           .max(Date::compareTo).orElse(currentDate);
				}

				logUpdate.put("lastUpdate", newLastUpdate.getTime());
				logUpdate.put("logs", newLogs.stream()
				                             .map(Log::toJSON)
				                             .collect(JSONArray::new, JSONArray::put, (array1, array2) -> array2.forEach(array1::put)));
			}

			if(request.has("updateConfigs")) {
				JSONObject configUpdate = new JSONObject();
				response.put("configUpdate", configUpdate);

				JSONArray updateConfigs = request.getJSONArray("updateConfigs");

				for(Object configNameObject : updateConfigs) {

					String configName = (String) configNameObject;

					Properties config = ConfigFactory.getConfig(configName);

					configUpdate.put(configName, ConfigFactory.configToJSON(config));
				}
			}

			if(request.has("saveConfigs")) {
				JSONObject saveConfigs = request.getJSONObject("saveConfigs");

				for(String configName : saveConfigs.keySet()) {

					Properties config = ConfigFactory.getConfig(configName);
					JSONObject newConfigJSON = saveConfigs.getJSONObject(configName);

					for(String newConfigKey : newConfigJSON.keySet()) {
						String newConfigValue = (String) newConfigJSON.get(newConfigKey);
						config.put(newConfigKey, newConfigValue);
					}

					ConfigFactory.saveConfig(configName, config);
				}

				WebInitializer.reinit();
			}

			client.write((response.toString() + "\nEndOfMessage\n").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
