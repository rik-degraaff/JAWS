package jaws.business.loggers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import jal.business.log.Log;

public class LogCache implements Consumer<Log> {

	private List<Log> cachedLogs;
	private int cacheSize;

	public LogCache(int cacheSize) {

		cachedLogs = new ArrayList<>();
		this.cacheSize = cacheSize;
	}

	@Override
	public void accept(Log log) {

		cachedLogs.add(log);
		if(cachedLogs.size() > cacheSize) {

			cachedLogs.remove(0);
		}
	}

	public List<Log> getLogs() {

		return new ArrayList<>(cachedLogs);
	}

	public List<Log> getLogsSince(Date timestamp) {

		return cachedLogs.stream().filter(log -> log.getTimeStamp().after(timestamp)).collect(Collectors.toList());
	}
}
