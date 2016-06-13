package jaws.business.loggers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import jal.business.log.Log;

/**
 * A class which caches all logs and offers an interface to access those.
 * 
 * @author Roy
 *
 */
public class LogCache implements Consumer<Log>, LogAccessor {

	private List<Log> cachedLogs;
	private int cacheSize;

	public LogCache(int cacheSize) {

		cachedLogs = new ArrayList<>();
		this.cacheSize = cacheSize;
	}

	/**
	 * This method is used to add a log to the cache.
	 */
	@Override
	public void accept(Log log) {

		cachedLogs.add(log);
		if(cachedLogs.size() > cacheSize) {

			cachedLogs.remove(0);
		}
	}

	@Override
	public List<Log> getLogs() {

		return new ArrayList<>(cachedLogs);
	}

	@Override
	public List<Log> getLogsSince(Date timestamp) {

		return cachedLogs.stream().filter(log -> log.getTimeStamp().after(timestamp)).collect(Collectors.toList());
	}
}
