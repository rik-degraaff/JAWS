package jaws.business.config;

import java.io.IOException;

import org.json.JSONObject;

import jaws.business.loggers.LogAccessor;
import jaws.context.Context;
import jaws.data.net.Connection;

public class ConfigRequestProcessor {

	private LogAccessor logCache;

	public ConfigRequestProcessor(LogAccessor logCache) {

		this.logCache = logCache;
	}

	public void handle(Connection client) {

		try {
			JSONObject request = new JSONObject(client.read());
		} catch (IOException e) {

		}
	}
}
