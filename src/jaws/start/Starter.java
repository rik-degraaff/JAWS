package jaws.start;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import jal.business.log.JALogger;
import jal.business.log.LogLevel;
import jaws.business.init.ConfigClientInitializer;
import jaws.business.init.LoggingInitializer;
import jaws.business.init.WebInitializer;
import jaws.context.Context;
import jaws.data.config.ConfigLoader;
import jaws.util.box.Box;

public class Starter {

	private static final String configFolder = "../config/";
	
	private static LogLevel logLevel = LogLevel.INFO;
	private static long timeout = 0;

	public static void main(String[] args) {

		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			if(Context.logger == null) {
				System.out.println("An uncaught error occured:" + System.lineSeparator()
                                   + sw.toString());
				System.out.println("Restarting");
			} else {
				Context.logger.error("An uncaught error occured:" + System.lineSeparator()
				                     + sw.toString());
				Context.logger.info("Restarting");
			}
			Thread thread = new Thread(() -> {
				deinit();
				init();
			});
			thread.start();
		});		
		
		processArgs(args);
		scheduleTimeout(timeout);
		init();
	}
	
	private static void scheduleTimeout(long timeout) {
		
		if (timeout > 0) {
			new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					Context.logger.info("Scheduled shutdown");
					deinit();
					System.exit(0);
				}
			}, timeout*1000);
		}
	}
	
	private static void processArgs(String[] args) {
		
		String last = "";
		for (int i = 0; i < args.length; i++) {
			if (Arrays.asList("--verbose", "-v").contains(args[i])) {
				logLevel = LogLevel.DEBUG;
			} else if (Arrays.asList("--quite", "-q").contains(args[i])) {
				logLevel = LogLevel.WARNING;
			} else if (Arrays.asList("--timeout", "-t").contains(last)) {
				timeout = Long.parseLong(args[i]);
			}
			last = args[i];
		}
	}

	private static void init() {

		if (!ConfigLoader.initialized()) ConfigLoader.init(configFolder);
		Box<JALogger> loggerBox = new Box<>();
		if (!LoggingInitializer.initialized()) LoggingInitializer.init(loggerBox, logLevel);
		if (!WebInitializer.initialized()) WebInitializer.init();
		if (!ConfigClientInitializer.initialized()) ConfigClientInitializer.init(loggerBox.unbox());
	}

	private static void deinit() {

		if (ConfigClientInitializer.initialized()) ConfigClientInitializer.deinit();
		if (WebInitializer.initialized()) WebInitializer.deinit();
		if (LoggingInitializer.initialized()) LoggingInitializer.deinit();
		if (ConfigLoader.initialized()) ConfigLoader.deinit();
	}
}
