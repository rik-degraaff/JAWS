package jaws.business.loggers;

import java.util.Date;
import java.util.List;

import jal.business.log.Log;

public interface LogAccessor {

	public List<Log> getLogs();

	public List<Log> getLogsSince(Date timestamp);
}
