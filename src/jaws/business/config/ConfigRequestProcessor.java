package jaws.business.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import jal.business.log.Log;
import jaws.business.loggers.LogAccessor;
import jaws.context.Context;
import jaws.data.net.Connection;

public class ConfigRequestProcessor {

	private LogAccessor logCache;

	public ConfigRequestProcessor(LogAccessor logCache) {

		this.logCache = logCache;
	}

	public void handle(Connection client) {

		JSONObject response = new JSONObject();

		try {
			StringBuilder stringBuilder = new StringBuilder();

			{
				String line;
				BufferedReader reader = client.read();
				while((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
			}

			JSONObject request = new JSONObject(stringBuilder.toString());
			Context.logger.debug(request.toString());

			if(request.has("updateLogs")) {
				JSONObject logUpdate = new JSONObject();
				response.put("logUpdate", logUpdate);

				JSONObject updateLogs = request.getJSONObject("updateLogs");
				Date lastUpdate;
				List<Log> newLogs;
				Date newLastUpdate;

				if(updateLogs.has("lastUpdate")) {
					lastUpdate = new Date(updateLogs.getInt("lastUpdate"));
					newLogs = logCache.getLogsSince(lastUpdate);
					newLastUpdate = newLogs.stream()
                            .map(log -> log.getTimeStamp())
                            .max(Date::compareTo).orElse(lastUpdate);
				} else {
					Date currentDate = new Date();
					newLogs = logCache.getLogs();
					newLastUpdate = newLogs.stream()
                            .map(log -> log.getTimeStamp())
                            .max(Date::compareTo).orElse(currentDate);
				}

				logUpdate.put("lastUpdate", newLastUpdate);
				logUpdate.put("logs", newLogs.stream()
				                              .map(Log::toJSON)
				                              .collect(JSONArray::new, JSONArray::put, (array1, array2) -> array2.forEach(array1::put)));
			}

			client.write(response.toString().getBytes());
		} catch (IOException e) {

		}
	}
}
