package jaws.start;

import java.io.PrintWriter;
import java.io.StringWriter;

import jaws.business.init.LoggingInitializer;
import jaws.business.init.WebInitializer;
import jaws.context.Context;

public class Starter {

	private static final String configFile = "../config";
	//private static final String logFile = "../logs";

	public static void main(String[] args) {
		
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Context.logger.error("An uncaught error occured:" + System.lineSeparator()
			                     + sw.toString());
			Context.logger.info("Restarting");
			Thread thread = new Thread(() -> {
				deinit();
				init();
			});
			thread.start();
		});
		
		init();
	}
	
	private static void init() {
		
		if (!LoggingInitializer.initialized()) LoggingInitializer.init();
		if (!WebInitializer.initialized()) WebInitializer.init(configFile);
	}
	
	private static void deinit() {
		
		if (WebInitializer.initialized()) WebInitializer.deinit();
		if (LoggingInitializer.initialized()) LoggingInitializer.deinit();
	}
	
	
}
