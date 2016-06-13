package jaws.business.loggers;

import java.util.Date;
import java.util.List;

import jal.business.log.Log;

/**
 * An interface for accessing a collection of logs.
 * 
 * @author Roy
 *
 */
public interface LogAccessor {

	/**
	 * Gets all available logs.
	 * 
	 * @return all available logs.
	 */
	public List<Log> getLogs();

	/**
	 * Gets all available logs whose timestamps are greater than the specified timestamp.
	 * 
	 * @param timestamp the timestamp used to filter the logs.
	 * @return the filtered logs.
	 */
	public List<Log> getLogsSince(Date timestamp);
}
