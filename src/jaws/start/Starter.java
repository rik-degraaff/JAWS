package jaws.start;

import java.io.PrintWriter;
import java.io.StringWriter;

import jal.business.log.JALogger;
import jaws.business.init.ConfigClientInitializer;
import jaws.business.init.LoggingInitializer;
import jaws.business.init.WebInitializer;
import jaws.context.Context;
import jaws.data.config.ConfigLoader;
import jaws.util.box.Box;

public class Starter {

	private static final String configFolder = "../config/";

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

		init();
	}

	private static void init() {

		if (!ConfigLoader.initialized()) ConfigLoader.init(configFolder);
		Box<JALogger> loggerBox = new Box<>();
		if (!LoggingInitializer.initialized()) LoggingInitializer.init(loggerBox);
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
